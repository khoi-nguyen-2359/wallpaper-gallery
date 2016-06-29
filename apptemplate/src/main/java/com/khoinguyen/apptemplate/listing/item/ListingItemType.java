package com.khoinguyen.apptemplate.listing.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 5/7/16.
 */
public abstract class ListingItemType<VH extends IViewHolder> {
  protected int viewType;
  protected LayoutInflater layoutInflater;

  protected LayoutInflater getLayoutInflater(View view) {
    return layoutInflater == null ? layoutInflater = LayoutInflater.from(view.getContext()) : layoutInflater;
  }

  public ListingItemType(int viewType) {
    this.viewType = viewType;
  }

  public abstract View createView(ViewGroup container);

  public abstract VH createViewHolder(View view);

  public int getViewType() {
    return viewType;
  }
}
