package com.khoinguyen.photoviewerkit.impl.event;

import android.graphics.RectF;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoListingItemClick {
  private final RectF fullRect;
  private PhotoDisplayInfo photoDisplayInfo;

  public OnPhotoListingItemClick(PhotoDisplayInfo photoDisplayInfo, RectF fullRect) {
    this.photoDisplayInfo = photoDisplayInfo;
    this.fullRect = fullRect;
  }

  public RectF getFullRect() {
    return fullRect;
  }

  public PhotoDisplayInfo getPhotoDisplayInfo() {
    return photoDisplayInfo;
  }
}
