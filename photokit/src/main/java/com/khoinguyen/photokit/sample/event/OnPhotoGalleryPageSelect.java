package com.khoinguyen.photokit.sample.event;

import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoGalleryPageSelect {
    private final int position;
    private final PhotoDisplayInfo photoDisplayInfo;

    public OnPhotoGalleryPageSelect(int position, PhotoDisplayInfo photoDisplayInfo) {
        this.position = position;
        this.photoDisplayInfo = photoDisplayInfo;
    }

    public int getPosition() {
        return position;
    }

    public PhotoDisplayInfo getPhotoDisplayInfo() {
        return photoDisplayInfo;
    }
}
