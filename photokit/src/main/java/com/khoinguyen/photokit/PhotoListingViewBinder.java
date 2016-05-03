package com.khoinguyen.photokit;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 4/29/16.
 */
public interface PhotoListingViewBinder<T> {
    int getItemCount();

    View createItemView(ViewGroup container, int itemIndex);

    void bindItemData(View itemView, int itemIndex);

    ViewHolder<T> createItemViewHolder(View itemView);

    T createItemData(int itemIndex);
}
