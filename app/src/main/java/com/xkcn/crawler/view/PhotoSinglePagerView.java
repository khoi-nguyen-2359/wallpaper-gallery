package com.xkcn.crawler.view;

import com.xkcn.crawler.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public interface PhotoSinglePagerView extends PhotoListingView {
    void setupPagerAdapter(List<PhotoDetails> photoDetailses);
}
