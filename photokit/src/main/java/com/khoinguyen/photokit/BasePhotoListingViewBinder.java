package com.khoinguyen.photokit;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class BasePhotoListingViewBinder<T> implements PhotoListingViewBinder<T> {
    private Map<View, ViewHolder<T>> mapViewHolder = new HashMap<>();
    private Map<Integer, T> mapDataCache = new HashMap<>();

    @Override
    public void bindItemData(View itemView, int itemIndex) {
        if (itemView == null) {
            return;
        }

        ViewHolder<T> vh = mapViewHolder.get(itemView);
        if (vh == null) {
            vh = createItemViewHolder(itemView);
            mapViewHolder.put(itemView, vh);
        }

        T data = mapDataCache.get(itemIndex);
        if (data == null) {
            data = createItemData(itemIndex);
            mapDataCache.put(itemIndex, data);
        }

        if (vh != null) {
            vh.bind(data);
        }
    }
}
