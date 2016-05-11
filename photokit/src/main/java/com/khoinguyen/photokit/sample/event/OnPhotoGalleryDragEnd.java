package com.khoinguyen.photokit.sample.event;

import android.graphics.RectF;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoGalleryDragEnd {
  private RectF fullRect;

  public OnPhotoGalleryDragEnd(RectF fullRect) {
    this.fullRect = fullRect;
  }

  public RectF getFullRect() {
    return fullRect;
  }
}
