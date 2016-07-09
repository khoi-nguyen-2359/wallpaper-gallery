package com.xkcn.gallery.imageloader;

import android.content.Context;
import android.net.Uri;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.memory.PooledByteBufferInputStream;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.util.AndroidUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by khoinguyen on 2/4/15.
 */
public final class PhotoFileManager {
  public static final String SUFF_DOWNLOAD_TMP_FILE = ".downloading";

  private File externalPhotoDir;
  private File internalPhotoDir;

  private L logger;

  private Map<String, Observable<Float>> mapWorkingObservables;

  private BaseApp baseApp;

  public PhotoFileManager(BaseApp baseApp) {
    this.baseApp = baseApp;
    resolveExternalPhotoDir(baseApp);
    resolveInternalPhotoDir(baseApp);
    logger = L.get(this);
    mapWorkingObservables = new HashMap<>();
  }

  private File resolveInternalPhotoDir(BaseApp baseApp) {
    if (internalPhotoDir != null) {
      return internalPhotoDir;
    }

    if (baseApp == null) {
      return null;
    }

    //// TODO: 7/9/16 TEST THIS
    try {
      internalPhotoDir = baseApp.getDir("photo", Context.MODE_PRIVATE);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return internalPhotoDir;
  }

  private File resolveExternalPhotoDir(BaseApp baseApp) {
    if (externalPhotoDir != null) {
      return externalPhotoDir;
    }

    if (baseApp == null) {
      return null;
    }

    File externalFilesDir = baseApp.getExternalFilesDir(null);
    if (externalFilesDir == null) {
      return null;
    }

    try {
      externalPhotoDir = new File(externalFilesDir.getAbsolutePath() + "/photo");
      if (!externalPhotoDir.exists()) {
        externalPhotoDir.mkdirs();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return externalPhotoDir;
  }

  private Observable<Float> getPhotoFileObservable(final String photoUrl) {
    Observable<Float> workingObservable = mapWorkingObservables.get(photoUrl);
    if (workingObservable != null) {
      logger.d("photo is being downloaded");
      return workingObservable.cache();
    }

    Observable<Float> getFileFromDiskObservable = Observable.create(new Observable.OnSubscribe<Float>() {
      @Override
      public void call(Subscriber<? super Float> subscriber) {
        logger.d("photo is being searched in disk");
        File downloadedFile = getDownloadFile(photoUrl);
        if (downloadedFile.exists()) {
          subscriber.onNext(0f);
          subscriber.onNext(1f);
        }
        subscriber.onCompleted();
      }
    });

    Observable<Float> saveFileFromFrescoObservable = Observable.create(new Observable.OnSubscribe<Float>() {
      @Override
      public void call(Subscriber<? super Float> subscriber) {
        logger.d("photo is being fetched by fresco thread=%s id=%s", Thread.currentThread().getName(), Thread.currentThread().getId());
        ImageRequest request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(photoUrl))
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(request, this);
        dataSource.subscribe(new PhotoDownloadSubscriber(subscriber, photoUrl), CallerThreadExecutor.getInstance());
      }
    });

    Observable<Float> result = Observable.concat(getFileFromDiskObservable, saveFileFromFrescoObservable)
        .takeUntil(new Func1<Float, Boolean>() {
          @Override
          public Boolean call(Float progress) {
            // each observable, one by one, must emit progress values in [0..1], so use "1" to indicate the download file is ready to use and stop the stream.
            return progress == 1;
          }
        })
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            mapWorkingObservables.remove(photoUrl);
            logger.d("end photo fetching");
          }
        });

    mapWorkingObservables.put(photoUrl, result);

    return result;
  }

  public File getDownloadFile(PhotoDetails photoDetails) {
    return getDownloadFile(photoDetails.getDefaultDownloadUrl());
  }

  class PhotoDownloadSubscriber extends BaseDataSubscriber<CloseableReference<PooledByteBuffer>> {

    private final Subscriber<? super Float> subscriber;
    private final String downloadUrl;

    PhotoDownloadSubscriber(Subscriber<? super Float> progressSubscriber, String photoUrl) {
      this.subscriber = progressSubscriber;
      this.downloadUrl = photoUrl;
    }

    @Override
    public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
      float progress = dataSource.getProgress();
//      logger.d("onProgressUpdate %f thread=%s id=%s", progress, Thread.currentThread().getName(), Thread.currentThread().getId());
      if (progress < 1) {
        // save value "1" for after saving file to disk
        subscriber.onNext(progress);
      }
    }

    @Override
    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
//      logger.d("onNewResultImpl thread=%s id=%s", Thread.currentThread().getName(), Thread.currentThread().getId());
      if (!dataSource.isFinished()) {
        return;
      }

      CloseableReference<PooledByteBuffer> cf = null;
      try {
        cf = dataSource.getResult();
        PooledByteBuffer buffer = cf.get();
        PooledByteBufferInputStream is = new PooledByteBufferInputStream(buffer);
        savePhoto(is, downloadUrl);
        is.close();
        subscriber.onNext(1f);
        subscriber.onCompleted();
      } catch (Exception e) {
        e.printStackTrace();
        subscriber.onError(e);
      } finally {
        CloseableReference.closeSafely(cf);
      }
    }

    @Override
    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
      subscriber.onError(dataSource.getFailureCause());
    }
  }

  public Observable<Float> getPhotoFileObservable(final PhotoDetails photoDetails) {
    final String downloadUrl = photoDetails.getDefaultDownloadUrl();
    return getPhotoFileObservable(downloadUrl);
  }

  private File savePhoto(InputStream is, String downloadUri) throws Exception {
    String fileName = AndroidUtils.getResourceName(downloadUri);
    File tmpFile = getDownloadFileWithFileName(fileName + SUFF_DOWNLOAD_TMP_FILE);

    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tmpFile));
    int read = 0;
    final int len = 8192;
    byte[] buffer = new byte[len];
    while ((read = is.read(buffer)) != -1) {
      output.write(buffer, 0, read);
    }

    output.flush();
    output.close();

    String tmpPath = tmpFile.getAbsolutePath();
    String fixedPath = tmpPath.replace(SUFF_DOWNLOAD_TMP_FILE, "");
    File fixedFile = new File(fixedPath);
    if (!tmpFile.renameTo(fixedFile)) {
      throw new Exception("Rename download temp file failed!");
    }

    return fixedFile;
  }

  public File getPhotoDir() {
    resolveExternalPhotoDir(baseApp);
    // check this in case there are errors in creating the external dir while external storage is writable.
    if (AndroidUtils.isExternalStorageWritable() && externalPhotoDir != null && externalPhotoDir.exists()) {
      return externalPhotoDir;
    }

    return resolveInternalPhotoDir(baseApp);
  }

  private File getDownloadFileWithFileName(String fileName) {
    return new File(String.format(Locale.US, "%s/%s", getPhotoDir().getAbsolutePath(), fileName));
  }

  private File getDownloadFile(String downloadUrl) {
    String fileName = AndroidUtils.getResourceName(downloadUrl);
    return getDownloadFileWithFileName(fileName);
  }
}
