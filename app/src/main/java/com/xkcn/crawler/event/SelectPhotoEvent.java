package com.xkcn.crawler.event;

import com.xkcn.crawler.db.Photo;

/**
 * Created by khoinguyen on 1/21/15.
 */
public class SelectPhotoEvent {
    private Photo photo;

    public SelectPhotoEvent(Photo p) {this.photo = p;}

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
