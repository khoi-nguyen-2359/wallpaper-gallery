package com.khoinguyen.photokit.sample.event;

import android.graphics.RectF;

import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoListingItemClick {
    private final RectF itemRect;
    private final RectF fullRect;
    private final int position;
    private PhotoDisplayInfo photoDisplayInfo;

    public OnPhotoListingItemClick(RectF itemRect, RectF fullRect, PhotoDisplayInfo photoDisplayInfo, int position) {
        this.itemRect = itemRect;
        this.fullRect = fullRect;
        this.photoDisplayInfo = photoDisplayInfo;
        this.position = position;
    }

    public RectF getItemRect() {
        return itemRect;
    }

    public RectF getFullRect() {
        return fullRect;
    }

    public int getPosition() {
        return position;
    }

    public PhotoDisplayInfo getPhotoDisplayInfo() {
        return photoDisplayInfo;
    }
}
