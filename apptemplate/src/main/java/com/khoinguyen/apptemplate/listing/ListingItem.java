package com.khoinguyen.apptemplate.listing;

/**
 * Created by khoinguyen on 5/7/16.
 */
public class ListingItem {
  private Object data;
  private ListingItemType listingItemType;

  public ListingItem(Object data, ListingItemType listingItemType) {
    this.data = data;
    this.listingItemType = listingItemType;
  }

  public Object getData() {
    return data;
  }

  public ListingItemType getListingItemType() {
    return listingItemType;
  }
}
