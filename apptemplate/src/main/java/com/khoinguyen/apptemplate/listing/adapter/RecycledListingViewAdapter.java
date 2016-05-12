package com.khoinguyen.apptemplate.listing.adapter;

import android.view.View;

import com.khoinguyen.apptemplate.listing.ItemViewHolder;
import com.khoinguyen.util.log.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 * <p/>
 * This adapter saves one viewholder for each item view created. Used in case item views are recycled.
 */
public abstract class RecycledListingViewAdapter<DATA> extends BaseListingViewAdapter<DATA> {
  protected Map<View, ItemViewHolder> mapViewHolderByView = new HashMap<>();
  private L log = L.get("RecycledListingViewAdapter");

  @Override
  public void bindData(View itemView, int itemIndex) {
    super.bindData(itemView, itemIndex);
    log.d("mapViewHolderByView.size=%d", mapViewHolderByView.size());
  }

  @Override
  public ItemViewHolder getViewHolder(View itemView, Object viewType) {
    ItemViewHolder viewHolder = mapViewHolderByView.get(itemView);
    if (viewHolder == null) {
      viewHolder = super.getViewHolder(itemView, viewType);
      log.d("createViewHolder viewType=%s", viewType);
      mapViewHolderByView.put(itemView, viewHolder);
    }

    return viewHolder;
  }
}
