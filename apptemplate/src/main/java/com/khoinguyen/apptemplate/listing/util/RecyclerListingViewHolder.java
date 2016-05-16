package com.khoinguyen.apptemplate.listing.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khoinguyen.apptemplate.listing.IViewHolder;

/**
 * Created by khoinguyen on 5/17/16.
 */
public abstract class RecyclerListingViewHolder<DATA> extends RecyclerView.ViewHolder implements IViewHolder<DATA> {
  public RecyclerListingViewHolder(View itemView) {
    super(itemView);
  }
}
