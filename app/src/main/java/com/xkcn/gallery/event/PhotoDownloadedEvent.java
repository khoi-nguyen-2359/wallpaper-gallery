package com.xkcn.gallery.event;

import android.net.Uri;

/**
 * Created by khoinguyen on 2/3/15.
 */
public class PhotoDownloadedEvent {
    private long photoIdentifier;
    private Uri uriDownloaded;

    public PhotoDownloadedEvent(long photoId, Uri uri) {
        uriDownloaded = uri;
        this.photoIdentifier = photoId;
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
}
