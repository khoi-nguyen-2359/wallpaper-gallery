package com.xkcn.crawler.photoactions;

import android.net.Uri;

import com.squareup.okhttp.internal.Util;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.util.L;
import com.xkcn.crawler.util.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private OkHttpDownloader downloader;
    private ThreadPoolExecutor executorService;

    private L logger;

    private PhotoDownloadManager() {
        downloadingUris = new HashSet<>();

        logger = L.get(PhotoDownloadManager.class.getName());

        executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());

        downloader = new OkHttpDownloader(XkcnApp.app);
    }

    void doDownload(long photoIdentifier, String photoUrl) {
        logger.d("start file downloading");
        downloadingUris.add(photoUrl);
        try {
            Downloader.Response response = downloader.load(Uri.parse(photoUrl), 0);   // from source of NetworkPolicy.java class
            InputStream responseInputStream = response.getInputStream();
            Uri downloadedUri = StorageUtils.savePhoto(responseInputStream, photoUrl);
            responseInputStream.close();

            EventBus.getDefault().post(new PhotoDownloadedEvent(photoIdentifier, downloadedUri));
        } catch (Exception e) {
            e.printStackTrace();

            EventBus.getDefault().post(new PhotoDownloadFailedEvent());
        } finally {
            downloadingUris.remove(photoUrl);
        }
        logger.d("end file downloading");
    }

    public boolean asyncDownload(long photoIdentifier, String photoUrl) {
        if (downloadingUris.contains(photoUrl)) {
            logger.d("file is being downloaded");
            return false;
        }

        File downloadedFile = StorageUtils.getReadablePhotoFile(photoUrl);
        if (downloadedFile.exists()) {
            logger.d("file already downloaded");
            EventBus.getDefault().post(new PhotoDownloadedEvent(photoIdentifier, Uri.fromFile(downloadedFile)));
            return true;
        }

        logger.d("start photo download");
        executorService.execute(new PhotoDownloadTask(photoIdentifier, photoUrl));

        return false;
    }
}
