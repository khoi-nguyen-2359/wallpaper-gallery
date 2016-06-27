package com.khoinguyen.photoviewerkit.impl.data;

/**
 * Created by khoinguyen on 5/12/16.
 */
public class SharedData {
  private ListingItemInfo lastActiveItem = new ListingItemInfo();
  private ListingItemInfo currentActiveItem = new ListingItemInfo();

  public ListingItemInfo getLastActiveItem() {
    return lastActiveItem;
  }

  public void setLastActiveItem(ListingItemInfo lastActiveItem) {
    this.lastActiveItem = lastActiveItem;
  }

  public ListingItemInfo getCurrentActiveItem() {
    return currentActiveItem;
  }

  public void setCurrentActiveItem(ListingItemInfo currentActiveItem) {
    this.currentActiveItem = currentActiveItem;
  }
}
