package com.xkcn.gallery.view;

import android.view.View;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListingView {
    void populatePhotoData(List<PhotoDetails> photos);

    void displayPhotoItem(int position);

    View getPhotoItemView(int position);
}
