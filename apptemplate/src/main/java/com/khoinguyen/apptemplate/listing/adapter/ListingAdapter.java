package com.khoinguyen.apptemplate.listing.adapter;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;

/**
 * Created by khoinguyen on 6/21/16.
 */
public abstract class ListingAdapter<VH extends IViewHolder > implements IListingAdapter<VH> {
  protected DataObservable dataObservable = new DataObservable();

  @Override
  public final void notifyDataSetChanged() {
    dataObservable.notifyChanged();
  }

  @Override
  public void registerDataObserver(DataObserver observer) {
    dataObservable.registerObserver(observer);
  }

  @Override
  public void unregisterDataObserver(DataObserver observer) {
    dataObservable.unregisterObserver(observer);
  }
}
