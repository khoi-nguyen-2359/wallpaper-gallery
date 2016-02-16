package com.xkcn.crawler.presenter;

import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.imageloader.PhotoDownloader;
import com.xkcn.crawler.view.PhotoSinglePageView;

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
