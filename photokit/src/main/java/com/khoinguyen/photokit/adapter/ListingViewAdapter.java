package com.khoinguyen.photokit.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 4/29/16.
 */
public interface ListingViewAdapter<DATA> {
    int getCount();
    DATA getData(int itemIndex);
    View getView(ViewGroup parentView, Object viewType);
    void bindData(View itemView, int itemIndex);
}
