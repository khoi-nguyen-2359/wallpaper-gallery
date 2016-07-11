package com.khoinguyen.apptemplate.listing.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;

/**
 * Created by khoinguyen on 5/17/16.
 */
public class RecyclerListingViewHolder<DATA> extends RecyclerView.ViewHolder implements IViewHolder<DATA> {
  public RecyclerListingViewHolder(View itemView) {
    super(itemView);
  }

  @Override
  public void bind(DATA data) {
    // do nothing to use this as a dummy view holder
  }

  @Override
  public void setViewType(int viewType) {
    // can not set, RecyclerView.Adapter does this job.
  }

  @Override
  public int getViewType() {
    return getItemViewType();
  }
}
