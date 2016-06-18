package com.khoinguyen.photoviewerkit.impl.data;

import android.graphics.RectF;

public class ListingItemInfo {
    private RectF itemRect = new RectF();
    private String photoId;

    public String getPhotoId() {
      return photoId;
    }

    public RectF getItemRect() {
      return itemRect;
    }

    public void updateItemRect(RectF itemRect) {
      this.itemRect.set(itemRect);
    }

    public void setPhotoId(String photoId) {
      this.photoId = photoId;
    }
  }