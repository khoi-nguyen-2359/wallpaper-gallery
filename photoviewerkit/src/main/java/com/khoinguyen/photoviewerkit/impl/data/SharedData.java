package com.khoinguyen.photoviewerkit.impl.data;

/**
 * Created by khoinguyen on 5/12/16.
 */
public class SharedData {
  private ListingItemInfo lastSelectedListingItem = new ListingItemInfo();
  private ListingItemInfo currentSelectedListingItem = new ListingItemInfo();

  public ListingItemInfo getLastSelectedItem() {
    return lastSelectedListingItem;
  }

  public void setLastSelectedListingItem(ListingItemInfo lastSelectedListingItem) {
    this.lastSelectedListingItem = lastSelectedListingItem;
  }

  public ListingItemInfo getCurrentSelectedItem() {
    return currentSelectedListingItem;
  }

  public void setCurrentSelectedListingItem(ListingItemInfo currentSelectedListingItem) {
    this.currentSelectedListingItem = currentSelectedListingItem;
  }
}
