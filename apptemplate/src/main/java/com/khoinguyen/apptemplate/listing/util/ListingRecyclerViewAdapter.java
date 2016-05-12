package com.khoinguyen.apptemplate.listing.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.adapter.RecycledListingViewAdapter;
import com.khoinguyen.util.log.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 5/10/16.
 */
public class ListingRecyclerViewAdapter extends RecyclerView.Adapter<ListingRecyclerViewAdapter.ViewHolder> {
  private RecycledListingViewAdapter listingAdapter;
  private Map<Object, Integer> mapViewTypeByIntValue = new HashMap<>();
  private Map<Integer, Object> mapIntValueByViewType = new HashMap<>();

  private L log = L.get("ListingRecyclerViewAdapter");

  public void setListingAdapter(RecycledListingViewAdapter listingAdapter) {
    this.listingAdapter = listingAdapter;
    buildViewTypeMap();
  }

  public void buildViewTypeMap() {
    mapViewTypeByIntValue.clear();
    mapIntValueByViewType.clear();
    int i = 0;
    for (Object type : listingAdapter.getAllViewTypes()) {
      mapViewTypeByIntValue.put(type, i);
      mapIntValueByViewType.put(i, type);
      ++i;
    }
    log.d("buildViewTypeMap i=%d", i);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    log.d("onCreateViewHolder");
    return new ViewHolder(listingAdapter.getView(parent, mapIntValueByViewType.get(viewType)));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    listingAdapter.bindData(holder.itemView, position);
  }

  @Override
  public int getItemCount() {
    return listingAdapter == null ? 0 : listingAdapter.getCount();
  }

  @Override
  public int getItemViewType(int position) {
    Object viewType = listingAdapter.getViewType(position);
    return mapViewTypeByIntValue.get(viewType);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
