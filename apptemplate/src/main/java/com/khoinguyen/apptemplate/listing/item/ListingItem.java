package com.khoinguyen.apptemplate.listing.item;

/**
 * Created by khoinguyen on 5/7/16.
 */
public class ListingItem {
  private Object data;
  private int viewType;

  public ListingItem(Object data, int viewType) {
    this.data = data;
    this.viewType = viewType;
  }

  public Object getData() {
    return data;
  }

  public int getViewType() {
    return viewType;
  }
}
