package com.xkcn.gallery.view;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListingView {
    void displayPhotoData(List<PhotoDetails> photos);
}
