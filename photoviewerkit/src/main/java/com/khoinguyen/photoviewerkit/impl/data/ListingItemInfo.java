package com.khoinguyen.photoviewerkit.impl.data;

import android.graphics.RectF;
import android.text.TextUtils;

public class ListingItemInfo {
  private RectF itemRect = new RectF();
  private PhotoDisplayInfo photo;

  public PhotoDisplayInfo getPhoto() {
    return photo;
  }

  public String getPhotoId() {
    return photo == null ? null : photo.getPhotoId();
  }

  public RectF getItemRect() {
    return itemRect;
  }

  public void updateItemRect(RectF itemRect) {
    this.itemRect.set(itemRect);
  }

  public void setPhoto(PhotoDisplayInfo photoId) {
      this.photo = photoId;
    }

  public boolean isPhotoValid() {
    return photo != null && !TextUtils.isEmpty(photo.getPhotoId());
  }
}