package com.khoinguyen.photokit;

import com.khoinguyen.photokit.binder.PhotoListingViewBinder;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListingView<BINDER extends PhotoListingViewBinder> {
    void setBinder(BINDER binder);
    void notifyDataSetChanged();
}
