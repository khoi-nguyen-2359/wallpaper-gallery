package com.khoinguyen.photokit;

/**
 * Created by khoinguyen on 5/3/16.
 */
public interface PhotoKitWidget {
    void setBinders(PhotoListingViewBinder listingBinder, PhotoListingViewBinder galleryBinder);

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
