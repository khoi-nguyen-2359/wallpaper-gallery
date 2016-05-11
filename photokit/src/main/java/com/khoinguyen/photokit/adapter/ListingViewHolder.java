package com.khoinguyen.photokit.adapter;

import android.view.View;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class ListingViewHolder<DATA> {
  protected View itemView;

  public ListingViewHolder(View itemView) {
    this.itemView = itemView;
  }

  public void bind(DATA data) {
  }

  public View getItemView() {
    return this.itemView;
  }

  public void prepare() {
  }

  public void unbind() {
  }
}
