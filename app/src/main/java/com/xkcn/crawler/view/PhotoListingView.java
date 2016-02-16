package com.xkcn.crawler.view;

import com.xkcn.crawler.data.model.PhotoDetails;

import java.io.File;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface PhotoListingView {
    void showToast(String message);

    void showLoading();
    void hideLoading();

    void showWallpaperChooser(File photoFile);
    int getCurrentType();
}
