package com.xkcn.gallery.event;

import android.graphics.RectF;

import com.xkcn.gallery.adapter.PhotoListingItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class OnPhotoListItemClicked {
    private int clickedPosition;
    private PhotoDetails photoDetails;
    private RectF startRect;

    public OnPhotoListItemClicked(int clickedPosition, PhotoDetails photoDetails, RectF startRect) {
        this.clickedPosition = clickedPosition;
        this.photoDetails = photoDetails;
        this.startRect = startRect;
    }

    public int getItemPosition() {
        return clickedPosition;
    }

    public PhotoDetails getPhotoDetails() {
        return photoDetails;
    }

    public RectF getStartRect() {
        return startRect;
    }
}
