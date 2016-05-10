package com.khoinguyen.photokit;

import com.khoinguyen.photokit.adapter.ListingViewAdapter;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListingView<BINDER extends ListingViewAdapter> {
    void setAdapter(BINDER binder);
    void notifyDataSetChanged();
}
