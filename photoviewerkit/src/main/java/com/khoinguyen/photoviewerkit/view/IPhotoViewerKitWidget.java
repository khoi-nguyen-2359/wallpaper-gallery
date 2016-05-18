package com.khoinguyen.photoviewerkit.view;

import com.khoinguyen.apptemplate.eventbus.IEventBus;

/**
 * Created by khoinguyen on 5/13/16.
 */
public interface IPhotoViewerKitWidget<D> {
  /**
   * Open gallery view at a specific photo item.
   * @param photoId id of the photo to be opened
   */
  void openGalleryView(String photoId);

  /**
   * Handle back button action.
   * @return False indicates nothing handled, otherwise True.
   */
  boolean handleBackPress();

  /**
   * The widget use event bus to fire events. This will create an event bus instance if hasnt created before and return.<br/>
   * Only one event bus is used for the whole widget and its components.
   * @return
   */
  IEventBus getEventBus();

  D getSharedData();
}
