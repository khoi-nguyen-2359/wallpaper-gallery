package com.khoinguyen.photokit.view;

import com.khoinguyen.photokit.presenter.PhotoListingViewPagerPresenter;

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
