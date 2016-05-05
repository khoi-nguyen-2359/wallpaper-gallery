package com.khoinguyen.photokit.sample.model;

import android.graphics.RectF;

/**
 * Created by khoinguyen on 5/4/16.
 */
public class PhotoListingItemTrackingInfo {
    private RectF itemRect = new RectF();
    private int itemIndex = -1;
    private PhotoDisplayInfo itemPhoto;

    public RectF getItemRect() {
        return itemRect;
    }

    public void updateItemRect(RectF itemRect) {
        this.itemRect.set(itemRect);
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public void set(PhotoListingItemTrackingInfo otherInfo) {
        updateItemRect(otherInfo.getItemRect());
        setItemIndex(otherInfo.getItemIndex());
        setItemPhoto(otherInfo.getItemPhoto());
    }

    public PhotoDisplayInfo getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(PhotoDisplayInfo itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public void reset() {
        itemIndex = -1;
        itemRect = new RectF();
        itemPhoto = null;
    }
}
