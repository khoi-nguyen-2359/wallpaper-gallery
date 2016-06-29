package com.khoinguyen.photoviewerkit.interfaces;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.pageable.IPageableListingView;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;

/**
 * Created by khoinguyen on 5/13/16.
 */
public interface IPhotoViewerKitWidget<D> extends IPageableListingView {
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

  void onPagingNext(IPageableListingView component);

  void revealGallery(int itemIndex);

  void returnToListing();

  interface PagingListener {
    void onPagingNext(IPhotoViewerKitWidget widget);
  }
}
