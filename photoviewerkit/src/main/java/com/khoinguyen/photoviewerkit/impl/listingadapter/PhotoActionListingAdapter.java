package com.khoinguyen.photoviewerkit.impl.listingadapter;

import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;

public abstract class PhotoActionListingAdapter<VH extends IViewHolder> extends PartitionedListingAdapter<VH> {
    public abstract int getActionId(int itemIndex);
  }