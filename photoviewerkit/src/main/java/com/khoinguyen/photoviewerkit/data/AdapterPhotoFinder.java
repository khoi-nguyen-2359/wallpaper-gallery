package com.khoinguyen.photoviewerkit.data;

import android.text.TextUtils;

import com.khoinguyen.apptemplate.listing.adapter.ListingAdapter;

/**
 * Created by khoinguyen on 5/9/16.
 */
public class AdapterPhotoFinder {
  private ListingAdapter adapter;

  public AdapterPhotoFinder(ListingAdapter adapter) {
    this.adapter = adapter;
  }

  public PhotoDisplayInfo getPhoto(int itemIndex) {
    Object data = adapter.getData(itemIndex);
    if (data == null || !(data instanceof PhotoDisplayInfo)) {
      return null;
    }

    return (PhotoDisplayInfo) data;
  }

  /**
   * Search index of item which has backed photo id matches the given id.<br/>
   *
   * @param photoId id to search
   * @return item index or -1 if not found
   */
  public int indexOf(String photoId) {
    if (TextUtils.isEmpty(photoId)) {
      return -1;
    }

    final int itemCount = adapter.getCount();
    for (int i = 0; i < itemCount; ++i) {
      PhotoDisplayInfo displayInfo = getPhoto(i);
      if (displayInfo == null) {
        continue;
      }

      if (photoId.equals(displayInfo.getPhotoId())) {
        return i;
      }
    }

    return -1;
  }

  public ListingAdapter getAdapter() {
    return adapter;
  }
}
