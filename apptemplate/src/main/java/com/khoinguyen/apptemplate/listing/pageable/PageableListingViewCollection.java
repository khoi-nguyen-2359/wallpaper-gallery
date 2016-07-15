package com.khoinguyen.apptemplate.listing.pageable;

import java.util.HashSet;

/**
 * Created by khoinguyen on 6/16/16.
 */

public class PageableListingViewCollection extends HashSet<IPageableListingView> {
  public void enablePaging() {
    for (IPageableListingView item : this) {
      if (item == null) {
        continue;
      }

      item.enablePaging();
    }
  }
}
