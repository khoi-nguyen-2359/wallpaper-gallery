package com.xkcn.crawler.photomanager;

import android.net.Uri;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.memory.PooledByteBufferInputStream;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.util.L;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 2/4/15.
 */
public final class PhotoDownloadManager {
    public static final int PURPOSE_CACHE_STORAGE = 1;
    public static final int PURPOSE_SET_WP = 2;

    private static PhotoDownloadManager instance;

    public static PhotoDownloadManager getInstance() {
        if (instance == null) {
            instance = new PhotoDownloadManager();
        }

        return instance;
    }

    private HashSet<String> downloadingUris;
    private ThreadPoolExecutor executorService;

    private L logger;

    private PhotoDownloadManager() {
        downloadingUris = new HashSet<>();

        logger = L.get(PhotoDownloadManager.class.getName());

        executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public boolean asyncDownload(final long photoIdentifier, final String photoUrl) {
        if (downloadingUris.contains(photoUrl)) {
            logger.d("photo is being downloaded");
            return false;
        }

        File downloadedFile = StorageUtils.getDownloadedPhotoFile(photoUrl);
        if (downloadedFile.exists()) {
            logger.d("photo already downloaded");
            EventBus.getDefault().post(new PhotoDownloadedEvent(photoIdentifier, Uri.fromFile(downloadedFile)));
            return true;
        }

        logger.d("start photo download");
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(photoUrl))
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
                    Uri downloadedUri = StorageUtils.savePhoto(is, photoUrl);
                    EventBus.getDefault().post(new PhotoDownloadedEvent(photoIdentifier, downloadedUri));
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new PhotoDownloadFailedEvent());
                } finally {
                    CloseableReference.closeSafely(cf);
                }

                logger.d("end file downloading");
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                logger.d("end file downloading");
                EventBus.getDefault().post(new PhotoDownloadFailedEvent());
            }
        }, executorService);

//        executorService.execute(new PhotoDownloadTask(photoIdentifier, photoUrl));

        return false;
    }
}
