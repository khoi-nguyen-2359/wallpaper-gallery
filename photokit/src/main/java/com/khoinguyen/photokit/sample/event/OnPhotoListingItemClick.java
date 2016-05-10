package com.khoinguyen.photokit.sample.event;

import android.graphics.RectF;

import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.sample.view.DefaultPhotoListingView;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoListingItemClick {
    private final RectF fullRect;
    private PhotoDisplayInfo photoDisplayInfo;
    private final DefaultPhotoListingView.PhotoListingItemTrackingInfo currentItemInfo;

    public OnPhotoListingItemClick(PhotoDisplayInfo photoDisplayInfo, DefaultPhotoListingView.PhotoListingItemTrackingInfo currentItemInfo, RectF fullRect) {
        this.photoDisplayInfo = photoDisplayInfo;
        this.currentItemInfo = currentItemInfo;
        this.fullRect = fullRect;
    }

    public RectF getFullRect() {
        return fullRect;
    }

    public DefaultPhotoListingView.PhotoListingItemTrackingInfo getCurrentItemInfo() {
        return currentItemInfo;
    }

    public PhotoDisplayInfo getPhotoDisplayInfo() {
        return photoDisplayInfo;
    }
}
