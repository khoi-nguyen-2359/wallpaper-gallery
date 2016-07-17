package com.khoinguyen.apptemplate.listing.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 5/7/16.
 */
public abstract class ListingItemType<VH extends IViewHolder> {
  protected int viewType;
  protected LayoutInflater layoutInflater;

  protected LayoutInflater getLayoutInflater(Context context) {
    return layoutInflater == null ? layoutInflater = LayoutInflater.from(context) : layoutInflater;
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
