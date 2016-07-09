package com.xkcn.gallery.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 6/18/16.
 */
public class DataPage<T> {
  private List<T> data = new ArrayList<>();
  private int start;
  private boolean hasEnded = false;

  public DataPage(List<T> items, int start) {
    append(items);
    this.start = start;
  }

  public DataPage() {
    this(null, 0);
  }

  public List<T> getData() {
    return data;
  }

  private void append(List<T> items) {
    if (items == null) {
      return;
    }

    if (!items.isEmpty()) {
      data.addAll(items);
    } else {
      hasEnded = true;
    }
  }

  /**
   *
   * @param nextPage pass a page with empty data will end this data page.
   */
  public void append(DataPage<T> nextPage) {
    if (nextPage == null) {
      return;
    }

    append(nextPage.getData());
  }

  public void prepend(DataPage<T> prevPage) {
    data.addAll(0, prevPage.data);
    start = prevPage.start;
  }

  public void reset() {
    data.clear();
    hasEnded = false;
    start = 0;
  }

  public int getStart() {
    return start;
  }

  public int getNextStart() {
    return start + data.size();
  }

  public boolean hasEnded() {
    return hasEnded;
  }
}
