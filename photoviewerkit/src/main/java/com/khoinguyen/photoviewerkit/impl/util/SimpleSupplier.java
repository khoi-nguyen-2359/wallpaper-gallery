package com.khoinguyen.photoviewerkit.impl.util;

import com.facebook.common.internal.Supplier;

/**
 * Created by khoinguyen on 7/11/16.
 */

public class SimpleSupplier<T> implements Supplier<T> {
  private T data;

  public SimpleSupplier(T data) {
    set(data);
  }

  public void set(T data) {
    this.data = data;
  }

  @Override
  public T get() {
    return data;
  }
}
