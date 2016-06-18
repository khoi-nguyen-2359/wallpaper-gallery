package com.xkcn.gallery.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 6/18/16.
 */
public class DataPage<T> {
  private List<T> data = new ArrayList<>();
  private int startIndex;

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }

  public void append(DataPage<T> nextPage) {
    data.addAll(nextPage.data);
  }

  public void prepend(DataPage<T> prevPage) {
    data.addAll(0, prevPage.data);
    startIndex = prevPage.startIndex;
  }

  public void clear() {
    data.clear();
    startIndex = 0;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getNextStartIndex() {
    return startIndex + data.size();
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }
}
