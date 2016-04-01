package com.xkcn.gallery.event;

import android.view.View;

import com.xkcn.gallery.adapter.PhotoListItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class OnPhotoListItemClicked {
    private int clickedPosition;
    private PhotoListItemAdapter.ViewHolder clickedView;
    private PhotoDetails photoDetails;

    public OnPhotoListItemClicked(int clickedPosition, PhotoListItemAdapter.ViewHolder v, PhotoDetails photoDetails) {
        this.clickedPosition = clickedPosition;
        clickedView = v;
        this.photoDetails = photoDetails;
    }

    public int getItemPosition() {
        return clickedPosition;
    }

    public PhotoListItemAdapter.ViewHolder getItemViewHolder() {
        return clickedView;
    }

    public PhotoDetails getPhotoDetails() {
        return photoDetails;
    }
}
