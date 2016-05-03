package com.khoinguyen.photokit.sample.event;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoGalleryDragStart {
    private int currentItem;

    public OnPhotoGalleryDragStart(int currentItem) {

        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }
}
