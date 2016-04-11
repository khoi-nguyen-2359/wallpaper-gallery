package com.xkcn.gallery.view;

import java.io.File;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface ActivityView {
    void showToast(String message);

    void showLoading();
    void hideLoading();

    void showWallpaperChooser(File photoFile);
}
