package com.khoinguyen.apptemplate.listing.item;

import android.view.View;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class BaseViewHolder<DATA> implements IViewHolder<DATA> {
  protected View itemView;

  public BaseViewHolder(View itemView) {
    this.itemView = itemView;
  }

  public void bind(DATA data) {
  }

  public View getItemView() {
    return this.itemView;
  }
}
