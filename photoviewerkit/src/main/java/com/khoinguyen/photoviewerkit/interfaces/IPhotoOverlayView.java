package com.khoinguyen.photoviewerkit.interfaces;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.listingadapter.PhotoActionListingAdapter;

/**
 * Created by khoinguyen on 6/20/16.
 */

public interface IPhotoOverlayView<D> extends IPhotoViewerKitComponent<D> {
  void setPhotoActionAdapter(PhotoActionListingAdapter actionAdapter);
  void bindPhoto(PhotoDisplayInfo photoDisplayInfo);
}
