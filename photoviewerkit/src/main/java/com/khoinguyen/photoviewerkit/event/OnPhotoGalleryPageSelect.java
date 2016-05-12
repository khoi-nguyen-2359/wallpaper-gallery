package com.khoinguyen.photoviewerkit.event;

import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoGalleryPageSelect {
  private final int itemIndex;
  private final PhotoDisplayInfo photoDisplayInfo;

  public OnPhotoGalleryPageSelect(int itemIndex, PhotoDisplayInfo photoDisplayInfo) {
    this.itemIndex = itemIndex;
    this.photoDisplayInfo = photoDisplayInfo;
  }

  public int getItemIndex() {
    return itemIndex;
  }

  public PhotoDisplayInfo getPhotoDisplayInfo() {
    return photoDisplayInfo;
  }
}
