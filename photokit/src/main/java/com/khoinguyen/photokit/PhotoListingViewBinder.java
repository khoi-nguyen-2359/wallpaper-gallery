package com.khoinguyen.photokit;

import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/29/16.
 */
public interface PhotoListingViewBinder {
    int getItemCount();

    View getItemView(ViewGroup container, int itemIndex);
    ItemViewHolder getItemViewHolder(View itemView);

    Object getItemData(int itemIndex);
    void bindItemData(View itemView, int itemIndex);

    PhotoDisplayInfo getPhotoDisplayInfo(int itemIndex);
    ItemViewHolder<PhotoDisplayInfo> getDefaultPhotoListingItemViewHolder(View itemView);
}
