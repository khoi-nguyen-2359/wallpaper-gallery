package com.khoinguyen.apptemplate.listing;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 5/7/16.
 */
public abstract class ListingItemType<VH extends IViewHolder> {
  protected int viewType;

  public ListingItemType(int viewType) {
    this.viewType = viewType;
  }

  public abstract View createView(ViewGroup container);

  public abstract VH createViewHolder(View view);

  public int getViewType() {
    return viewType;
  }
}
