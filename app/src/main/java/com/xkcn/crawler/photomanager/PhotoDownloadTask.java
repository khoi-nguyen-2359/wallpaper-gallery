package com.xkcn.crawler.photomanager;

/**
 * Created by khoinguyen on 2/10/15.
 */
public class PhotoDownloadTask implements Runnable {
    private long photoIdentifier;
    private String photoUrl;

    public PhotoDownloadTask(long id, String url) {
        photoIdentifier = id;
        photoUrl = url;
    }

    @Override
    public void run() {
        PhotoDownloadManager downloadManager = PhotoDownloadManager.getInstance();
        downloadManager.doDownload(photoIdentifier, photoUrl);
    }
}
