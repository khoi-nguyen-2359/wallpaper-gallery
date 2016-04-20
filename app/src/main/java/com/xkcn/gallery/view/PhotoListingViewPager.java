package com.xkcn.gallery.view;

import android.support.v4.view.WindowInsetsCompat;

import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;

/**
 * Created by khoinguyen on 4/11/16.
 */
public interface PhotoListingViewPager {
    void setPresenter(PhotoListingViewPagerPresenter presenter);

    void displayPhotoPages(int pageCount, int botInset, int listingPerPage, int type);

    void setCurrentPage(int page);

    int getCurrentPage();

    void changeListingType(int type);
}
