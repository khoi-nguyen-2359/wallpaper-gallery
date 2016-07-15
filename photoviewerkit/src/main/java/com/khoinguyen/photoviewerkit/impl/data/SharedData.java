package com.khoinguyen.photoviewerkit.impl.data;

import android.graphics.RectF;

import com.khoinguyen.photoviewerkit.impl.view.PhotoViewerKitWidget;

import static com.khoinguyen.photoviewerkit.impl.view.PhotoViewerKitWidget.TRANS_LISTING;

/**
 * Created by khoinguyen on 5/12/16.
 */
public class SharedData {
  private ListingItemInfo lastActiveItem = new ListingItemInfo();
  private ListingItemInfo currentActiveItem = new ListingItemInfo();

  @PhotoViewerKitWidget.TransitionState
  private int currentTransitionState = TRANS_LISTING;

  public ListingItemInfo getLastActiveItem() {
    return lastActiveItem;
  }

  public ListingItemInfo getCurrentActiveItem() {
    return currentActiveItem;
  }

  public int getCurrentTransitionState() {
    return currentTransitionState;
  }

  public void setCurrentTransitionState(int currentTransitionState) {
    this.currentTransitionState = currentTransitionState;
  }

  public void activePhoto(PhotoDisplayInfo activeItem) {
    lastActiveItem.setPhoto(currentActiveItem.getPhoto());
    lastActiveItem.updateItemRect(currentActiveItem.getItemRect());

    currentActiveItem.setPhoto(activeItem);
  }

  /**
   * Are there interactions so that the active item has changed to another position?
   * @return
   */
  public boolean hasActiveItemChanged() {
    String lastActivePhotoId = lastActiveItem.getPhotoId();
    String currentActivePhotoId = currentActiveItem.getPhotoId();
    return lastActivePhotoId == null || !lastActivePhotoId.equals(currentActivePhotoId);
  }
}
