package com.khoinguyen.photokit.adapter;

/**
 * Created by khoinguyen on 5/7/16.
 */
public class PartDefinition {
  private Object data;
  private Object viewType;

  public PartDefinition(Object data, Object viewType) {
    this.data = data;
    this.viewType = viewType;
  }

  public PartDefinition(Object data, int viewType) {
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
