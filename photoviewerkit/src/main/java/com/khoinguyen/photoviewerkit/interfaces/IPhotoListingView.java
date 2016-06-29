package com.khoinguyen.photoviewerkit.interfaces;

import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
import com.khoinguyen.apptemplate.listing.pageable.IPageableListingView;

/**
 * Created by khoinguyen on 5/13/16.
 */
public interface IPhotoListingView<D, VH extends IViewHolder> extends IPhotoViewerKitComponent<D>, IPageableListingView {
  void toggleActiveItems();

  void activatePhotoItem(int position);
}
