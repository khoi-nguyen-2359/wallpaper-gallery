package com.khoinguyen.apptemplate.listing.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;

/**
 * Created by khoinguyen on 4/29/16.
 */
public interface IListingAdapter<VH extends IViewHolder> {
  void notifyDataSetChanged();
  void registerDataObserver(DataObserver observer);
  void unregisterDataObserver(DataObserver observer);

  int getCount();

  Object getData(int itemIndex);

  View getView(ViewGroup parentView, int viewType);

  int getViewType(int itemIndex);

  int getItemId(int itemIndex);

  VH getViewHolder(View itemView, int viewType);
}
