package com.xkcn.gallery.view;

import android.support.v4.view.WindowInsetsCompat;

import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;

/**
 * Created by khoinguyen on 4/11/16.
 */
public interface PhotoListingViewPager {
    void setPresenter(PhotoListingViewPagerPresenter presenter);

    void populatePhotoData(int pageCount, int listingPerPage, int type);

    void displayPage(int page);

    int getCurrentPagePosition();

    PhotoListingView getCurrentPageView();

    void changeListingType(int type);
}
