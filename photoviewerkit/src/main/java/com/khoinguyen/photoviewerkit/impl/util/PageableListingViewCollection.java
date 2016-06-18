package com.khoinguyen.photoviewerkit.impl.util;

import com.khoinguyen.photoviewerkit.impl.view.IPageableListingView;

import java.util.HashSet;

/**
 * Created by khoinguyen on 6/16/16.
 */

public class PageableListingViewCollection extends HashSet<IPageableListingView> {
  public void notifyPagingLoaded() {
    for (IPageableListingView item : this) {
      if (item == null) {
        continue;
      }

      item.setPagingLoaded();
    }
  }
}
