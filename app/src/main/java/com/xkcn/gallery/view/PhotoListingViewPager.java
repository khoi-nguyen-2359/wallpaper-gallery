package com.xkcn.gallery.view;

import android.support.v4.view.WindowInsetsCompat;

/**
 * Created by khoinguyen on 4/11/16.
 */
public interface PhotoListingViewPager {
    void displayPhotoPages(int pageCount, int type);

    void setCurrentPage(int page);

    int getCurrentPage();

    void onApplyWindowInsets(WindowInsetsCompat insets);
}
