package com.xkcn.crawler.event;

import android.net.Uri;

/**
 * Created by khoinguyen on 2/3/15.
 */
public class PhotoDownloadedEvent {
    private long photoIdentifier;
    private int purpose;
    private Uri uriDownloaded;

    public PhotoDownloadedEvent(long photoId, int purpose, Uri uri) {
        uriDownloaded = uri;
        this.photoIdentifier = photoId;
        this.purpose = purpose;
    }

    public Uri getDownloadedUri() {
        return uriDownloaded;
    }

    public void setUriDownloaded(Uri uriDownloaded) {
        this.uriDownloaded = uriDownloaded;
    }

    public long getPhotoIdentifier() {
        return photoIdentifier;
    }

    public void setPhotoIdentifier(long photoIdentifier) {
        this.photoIdentifier = photoIdentifier;
    }

    public int getPurpose() {
        return purpose;
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }
}
