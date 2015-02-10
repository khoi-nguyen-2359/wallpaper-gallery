package com.xkcn.crawler.photoactions;

import android.net.Uri;

import com.squareup.picasso.Downloader;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.util.StorageUtils;

import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 2/10/15.
 */
public class PhotoDownloadTask implements Runnable {
    private long photoIdentifier;
    private String photoUrl;
    private int purpose;

    public PhotoDownloadTask(long id, String url, int purpose) {
        photoIdentifier = id;
        photoUrl = url;
        this.purpose = purpose;
    }

    @Override
    public void run() {
        PhotoDownloadManager downloadManager = PhotoDownloadManager.getInstance();
        downloadManager.doDownload(photoIdentifier, photoUrl, purpose);
    }
}
