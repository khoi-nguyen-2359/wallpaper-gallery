package com.xkcn.crawler.usecase;

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
import com.fantageek.toolkit.util.L;
import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.usecase.error.PhotoDownloadFailedError;
import com.xkcn.crawler.usecase.error.PhotoDownloadInProgressError;
import com.xkcn.crawler.util.AndroidUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 2/4/15.
 */
public final class PhotoDownloader {
    public static final String SUFF_DOWNLOAD_TMP_FILE = "downloading.";

    private HashSet<String> downloadingUris;

    private final String externalFileDirPath;
    private final String photoDirPath;

    public PhotoDownloader(XkcnApp xkcnApp) {
        downloadingUris = new HashSet<>();
        externalFileDirPath = xkcnApp.getExternalFilesDir(null).getAbsolutePath();
        photoDirPath = xkcnApp.getDir("photo", Context.MODE_PRIVATE).getAbsolutePath();
    }

    private Observable<PhotoDetails> createPhotoDownloadObservable(final PhotoDetails photoDetails, final String downloadUrl) {
        return Observable.create(new Observable.OnSubscribe<PhotoDetails>() {
            @Override
            public void call(final Subscriber<? super PhotoDetails> subscriber) {
                final L logger = L.get();
                if (downloadingUris.contains(downloadUrl)) {
                    logger.d("photo is being downloaded");
                    subscriber.onError(new PhotoDownloadInProgressError());
                    return;
                }

                File downloadedFile = getDownloadFile(downloadUrl);
                if (downloadedFile.exists()) {
                    logger.d("photo already downloaded");
                    subscriber.onNext(photoDetails);
                    subscriber.onCompleted();
                    return;
                }

                logger.d("start photo download");
                downloadingUris.add(downloadUrl);
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(downloadUrl))
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();

                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(request, this);
                dataSource.subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
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
                            savePhoto(is, downloadUrl);
                            subscriber.onNext(photoDetails);
                            subscriber.onCompleted();
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            subscriber.onError(new PhotoDownloadFailedError());
                        } finally {
                            CloseableReference.closeSafely(cf);
                            downloadingUris.remove(downloadUrl);
                        }

                        logger.d("end file downloading");
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        logger.d("end file downloading");
                        subscriber.onError(new PhotoDownloadFailedError());
                        downloadingUris.remove(downloadUrl);
                    }
                }, CallerThreadExecutor.getInstance());
            }
        });
    }

    public Observable<PhotoDetails> createPhotoDownloadObservable(final PhotoDetails photoDetails) {
        final String downloadUrl = photoDetails.getDefaultDownloadUrl();
        return createPhotoDownloadObservable(photoDetails, downloadUrl);
    }

    private void savePhoto(InputStream is, String downloadUri) throws Exception {
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
