package com.xkcn.crawler.event;

import com.xkcn.crawler.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 2/11/15.
 */
public class SetWallpaperClicked {
    private PhotoDetails photo;
    public SetWallpaperClicked(PhotoDetails p) {
        photo = p;
    }

    public PhotoDetails getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoDetails photo) {
        this.photo = photo;
    }
}
