package com.khoinguyen.photokit.binder;

import android.view.View;

import com.khoinguyen.photokit.ItemViewHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class BasePhotoListingViewBinder implements PhotoListingViewBinder {
    private Map<View, ItemViewHolder> mapViewHolder = new HashMap<>();
    private Map<Integer, Object> mapDataCache = new HashMap<>();

    protected abstract Object createItemData(int itemIndex);
    protected abstract ItemViewHolder createItemViewHolder(View itemView);

    @Override
    public void bindItemData(View itemView, int itemIndex) {
        if (itemView == null) {
            return;
        }

        ItemViewHolder vh = getItemViewHolder(itemView);
        Object data = getItemData(itemIndex);
        if (vh != null) {
            vh.bindItemData(itemIndex, data);
        }
    }

    @Override
    public Object getItemData(int itemIndex) {
        Object data = mapDataCache.get(itemIndex);
        if (data == null) {
            data = createItemData(itemIndex);
            mapDataCache.put(itemIndex, data);
        }

        return data;
    }

    @Override
    public ItemViewHolder getItemViewHolder(View itemView) {
        if (itemView == null) {
            return null;
        }

        ItemViewHolder vh = mapViewHolder.get(itemView);
        if (vh == null) {
            vh = createItemViewHolder(itemView);
            mapViewHolder.put(itemView, vh);
        }

        return vh;
    }
}
