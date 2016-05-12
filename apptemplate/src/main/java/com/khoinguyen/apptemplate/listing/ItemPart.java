package com.khoinguyen.apptemplate.listing;

/**
 * Created by khoinguyen on 5/7/16.
 */
public class ItemPart {
  private Object data;
  private Object viewType;

  public ItemPart(Object data, Object viewType) {
    this.data = data;
    this.viewType = viewType;
  }

  public ItemPart(Object data, int viewType) {
    this.data = data;
    this.viewType = viewType;
  }

  public Object getData() {
    return data;
  }

  public Object getViewType() {
    return viewType;
  }
}
