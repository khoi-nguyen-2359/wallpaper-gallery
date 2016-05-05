package com.khoinguyen.photokit.sample.event;

import android.graphics.RectF;

import com.khoinguyen.photokit.sample.model.PhotoListingItemTrackingInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoListingItemClick {
    private final RectF fullRect;
    private final PhotoListingItemTrackingInfo currentItemInfo;

    public OnPhotoListingItemClick(PhotoListingItemTrackingInfo currentItemInfo, RectF fullRect) {
        this.currentItemInfo = currentItemInfo;
        this.fullRect = fullRect;
    }

    public RectF getFullRect() {
        return fullRect;
    }

    public PhotoListingItemTrackingInfo getCurrentItemInfo() {
        return currentItemInfo;
    }
}
