package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.imageloader.PhotoDownloader;
import com.xkcn.gallery.view.PhotoSinglePageView;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoSinglePageViewPresenter {
    private PhotoSinglePageView view;
    private PhotoDetails photoDetails;
    private PhotoDownloader photoDownloader;

    public PhotoSinglePageViewPresenter(PhotoDetails photoDetails, PhotoDownloader photoDownloader) {
        this.photoDetails = photoDetails;
        this.photoDownloader = photoDownloader;
    }

    public void setView(PhotoSinglePageView view) {
        this.view = view;
    }
}
