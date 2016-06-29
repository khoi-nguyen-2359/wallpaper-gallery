package com.khoinguyen.photoviewerkit.interfaces;

import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.view.PhotoActionView;

/**
 * Created by khoinguyen on 6/20/16.
 */

public interface IPhotoOverlayView<D> extends IPhotoViewerKitComponent<D> {
  void setPhotoActionAdapter(IListingAdapter actionAdapter);

  void setPhotoActionEventListener(PhotoActionView.PhotoActionEventListener eventListener);

  void bindPhoto(PhotoDisplayInfo photoDisplayInfo);

  void show();
  void hide();
}
