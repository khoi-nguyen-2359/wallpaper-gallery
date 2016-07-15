package com.khoinguyen.apptemplate.listing.item;

import android.view.View;

/**
 * Created by khoinguyen on 4/29/16.
 */
public class BaseViewHolder<DATA> implements IViewHolder<DATA> {
  protected View itemView;
  protected int viewType;

  public BaseViewHolder(View itemView) {
    this.itemView = itemView;
  }

  public void bind(DATA data) {
  }

  @Override
  public void setViewType(int viewType) {
    this.viewType = viewType;
  }

  @Override
  public int getViewType() {
    return viewType;
  }
}
