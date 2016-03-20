package com.xkcn.gallery.view;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface PhotoSinglePagerView extends PhotoListingView {
    void setupPagerAdapter(List<PhotoDetails> photoDetailses);
}
