package com.khoinguyen.apptemplate.listing;

/**
 * Created by khoinguyen on 5/7/16.
 */
public class ItemPart {
  private Object data;
  private int viewType;

  public ItemPart(Object data, int viewType) {
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
