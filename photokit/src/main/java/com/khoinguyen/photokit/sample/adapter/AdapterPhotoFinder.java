package com.khoinguyen.photokit.sample.adapter;

import android.text.TextUtils;

import com.khoinguyen.photokit.adapter.ListingViewAdapter;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 5/9/16.
 */
public class AdapterPhotoFinder {
  private ListingViewAdapter<PhotoDisplayInfo> adapter;

  public AdapterPhotoFinder(ListingViewAdapter<PhotoDisplayInfo> adapter) {
    this.adapter = adapter;
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
      PhotoDisplayInfo data = adapter.getData(i);
      if (data == null) {
        continue;
      }

      if (photoId.equals(data.getPhotoId())) {
        return i;
      }
    }

    return -1;
  }

  public ListingViewAdapter<PhotoDisplayInfo> getAdapter() {
    return adapter;
  }
}
