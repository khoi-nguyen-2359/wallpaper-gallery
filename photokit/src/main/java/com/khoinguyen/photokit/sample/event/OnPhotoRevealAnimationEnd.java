package com.khoinguyen.photokit.sample.event;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoRevealAnimationEnd {
    private int itemPosition;

    public OnPhotoRevealAnimationEnd(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public int getItemPosition() {
        return itemPosition;
    }
}
