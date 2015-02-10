package com.xkcn.crawler.event;

import com.xkcn.crawler.db.Photo;

/**
 * Created by khoinguyen on 2/11/15.
 */
public class SetWallpaperClicked {
    private Photo photo;
    public SetWallpaperClicked(Photo p) {
        photo = p;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
