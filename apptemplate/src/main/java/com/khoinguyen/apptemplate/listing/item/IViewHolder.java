package com.khoinguyen.apptemplate.listing.item;

/**
 * Created by khoinguyen on 5/16/16.
 */
public interface IViewHolder<DATA> {
  void bind(DATA data);

  void setViewType(int viewType);

  int getViewType();
}
