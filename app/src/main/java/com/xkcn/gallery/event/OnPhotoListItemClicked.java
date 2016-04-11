package com.xkcn.gallery.event;

import com.xkcn.gallery.adapter.PhotoListingItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class OnPhotoListItemClicked {
    private int clickedPosition;
    private PhotoListingItemAdapter.ViewHolder clickedView;
    private PhotoDetails photoDetails;

    public OnPhotoListItemClicked(int clickedPosition, PhotoListingItemAdapter.ViewHolder v, PhotoDetails photoDetails) {
        this.clickedPosition = clickedPosition;
        clickedView = v;
        this.photoDetails = photoDetails;
    }

    public int getItemPosition() {
        return clickedPosition;
    }

    public PhotoListingItemAdapter.ViewHolder getItemViewHolder() {
        return clickedView;
    }

    public PhotoDetails getPhotoDetails() {
        return photoDetails;
    }
}
