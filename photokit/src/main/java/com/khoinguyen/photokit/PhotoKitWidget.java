package com.khoinguyen.photokit;

import com.khoinguyen.photokit.binder.PhotoListingViewBinder;

/**
 * Created by khoinguyen on 5/3/16.
 */
public interface PhotoKitWidget<BINDER extends PhotoListingViewBinder> {
    void setBinders(BINDER listingBinder, BINDER galleryBinder);

    boolean handleBackPressed();

    TransitionState getTransitionState();

    void notifyDataSetChanged();

    enum TransitionState {
        LISTING,
        TO_DETAILS,
        DETAILS,
        TO_LISTING
    }
}
