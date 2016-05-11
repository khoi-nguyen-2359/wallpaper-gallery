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

/**
 * Created by khoinguyen on 2/4/15.
 */
public final class PhotoDownloader {
  public static final String SUFF_DOWNLOAD_TMP_FILE = "downloading.";

  private final String externalFileDirPath;
  private final String photoDirPath;
  private L logger;

  private Map<String, Observable<File>> mapDownloadObservables;

  public PhotoDownloader(BaseApp baseApp) {
    externalFileDirPath = baseApp.getExternalFilesDir(null).getAbsolutePath();
    photoDirPath = baseApp.getDir("photo", Context.MODE_PRIVATE).getAbsolutePath();
    logger = L.get(this);
    mapDownloadObservables = new HashMap<>();
  }

  public Observable<File> getPhotoDownloadObservable(final String downloadUrl) {
    Observable<File> downloadingObservable = mapDownloadObservables.get(downloadUrl);
    if (downloadingObservable != null) {
      logger.d("photo is being downloaded");
      return downloadingObservable.cache();
    }

    Observable<File> getFileFromDiskObservable = Observable.create(new Observable.OnSubscribe<File>() {
      @Override
      public void call(Subscriber<? super File> subscriber) {
        logger.d("photo is being searched in disk");
        File downloadedFile = getDownloadFile(downloadUrl);
        if (downloadedFile.exists()) {
          subscriber.onNext(downloadedFile);
        }
        subscriber.onCompleted();
      }
    });

    Observable<File> saveFileFromFrescoObservable = Observable.create(new Observable.OnSubscribe<File>() {
      @Override
      public void call(Subscriber<? super File> subscriber) {
        logger.d("photo is beging fetched from fresco");
        ImageRequest request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(downloadUrl))
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(request, this);
        dataSource.subscribe(new PhotoDownloadSubscriber(subscriber, downloadUrl), CallerThreadExecutor.getInstance());
      }
    });

    Observable<File> result = Observable.concat(getFileFromDiskObservable, saveFileFromFrescoObservable)
        .first()
        .doOnTerminate(new Action0() {
          @Override
          public void call() {
            mapDownloadObservables.remove(downloadUrl);
            logger.d("end photo fetching");
          }
        });

    mapDownloadObservables.put(downloadUrl, result);

    return result;
  }

  class PhotoDownloadSubscriber extends BaseDataSubscriber<CloseableReference<PooledByteBuffer>> {

    private final Subscriber<? super File> subscriber;
    private final String downloadUrl;

    PhotoDownloadSubscriber(Subscriber<? super File> subscriber, String photoUrl) {
      this.subscriber = subscriber;
      this.downloadUrl = photoUrl;
    }

    @Override
    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
      if (!dataSource.isFinished()) {
        return;
      }

      CloseableReference<PooledByteBuffer> cf = null;
      try {
        cf = dataSource.getResult();
        PooledByteBuffer buffer = cf.get();
        PooledByteBufferInputStream is = new PooledByteBufferInputStream(buffer);
        File savedFile = savePhoto(is, downloadUrl);
        subscriber.onNext(savedFile);
        is.close();
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

  public Observable<File> getPhotoDownloadObservable(final PhotoDetails photoDetails) {
    final String downloadUrl = photoDetails.getDefaultDownloadUrl();
    return getPhotoDownloadObservable(downloadUrl);
  }

  private File savePhoto(InputStream is, String downloadUri) throws Exception {
    String fileName = AndroidUtils.getResourceName(downloadUri);
    File tmpFile = getDownloadTempFile(fileName);

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

  private File getExternalPhotoDir() {
    File photoDir = new File(externalFileDirPath + "/photo");
    if (!photoDir.exists()) {
      //todo: what if failed to mkdirs
      photoDir.mkdirs();
    }

    return photoDir;
  }

  private File getDownloadTempFile(String fileName) {
    // first check app external storage
    File photoFile = null;
    if (AndroidUtils.isExternalStorageWritable()) {
      return new File(String.format(Locale.US, "%s/%s%s", getExternalPhotoDir().getAbsolutePath(), SUFF_DOWNLOAD_TMP_FILE, fileName));
    }

    // second check app internal storage
    return new File(String.format(Locale.US, "%s/%s%s", photoDirPath, SUFF_DOWNLOAD_TMP_FILE, fileName));
  }

  public File getDownloadFile(String downloadUrl) {
    String fileName = AndroidUtils.getResourceName(downloadUrl);
    File photoFile = null;
    if (AndroidUtils.isExternalStorageReadable()) {
      photoFile = new File(getExternalPhotoDir().getAbsolutePath() + "/" + fileName);
      if (photoFile.exists()) {
        return photoFile;
      }
    }

    // second check app internal storage
    return new File(photoDirPath + "/" + fileName);
  }
}
