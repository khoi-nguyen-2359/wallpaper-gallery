package com.khoinguyen.photokit;

/**
 * Created by khoinguyen on 4/25/16.
 */
public interface PhotoGalleryView {
    void setBinder(PhotoListingViewBinder galleryBinder);
    void notifyDataSetChanged();
}
