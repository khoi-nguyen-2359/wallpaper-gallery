package com.khoinguyen.photoviewerkit.impl.view;

import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 5/13/16.
 */
public interface IPhotoViewerKitPageableWidget<D> extends IPhotoViewerKitWidget<D>, IPageableListingView {
  void onPagingNext(IPageableListingView component);

  interface PagingListener {
    void onPagingNext(IPhotoViewerKitPageableWidget widget);
  }
}
