package com.khoinguyen.photokit;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListingView {
    void setBinder(PhotoListingViewBinder binder);

    void notifyDataSetChanged();
}
