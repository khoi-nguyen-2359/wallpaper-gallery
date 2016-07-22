package com.khoinguyen.photoviewerkit.interfaces;

import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.view.PhotoActionView;

/**
 * Created by khoinguyen on 6/20/16.
 */

public interface IPhotoOverlayView<D> extends IPhotoViewerKitComponent<D> {
  void setPhotoActionAdapter(IListingAdapter<IViewHolder<PhotoDisplayInfo>> actionAdapter);

  void setPhotoActionEventListener(PhotoActionView.PhotoActionEventListener eventListener);

  void bindPhoto(PhotoDisplayInfo photoDisplayInfo);

  void toggleFading();

  void show();
  void hide();
}
