package com.khoinguyen.photokit.adapter;

import android.view.View;

import com.khoinguyen.util.log.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 *
 * This adapter saves one viewholder for each item view created. Used in case item views are recycled.
 */
public abstract class RecycledListingViewAdapter<DATA> extends BaseListingViewAdapter<DATA> {
    protected Map<View, ListingViewHolder> mapViewHolderByType = new HashMap<>();
    private L log = L.get("RecycledListingViewAdapter");

    @Override
    public void bindData(View itemView, int itemIndex) {
        super.bindData(itemView, itemIndex);
        log.d("mapViewHolderByType.size=%d", mapViewHolderByType.size());
    }

    @Override
    public ListingViewHolder getViewHolder(View itemView, Object viewType) {
        ListingViewHolder viewHolder = mapViewHolderByType.get(itemView);
        if (viewHolder == null) {
            viewHolder = super.getViewHolder(itemView, viewType);
            log.d("createViewHolder viewType=%s", viewType);
            mapViewHolderByType.put(itemView, viewHolder);
        }

        return viewHolder;
    }
}
