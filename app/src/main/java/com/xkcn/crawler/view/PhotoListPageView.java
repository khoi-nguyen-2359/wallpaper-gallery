package com.xkcn.crawler.view;

import com.xkcn.crawler.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/18/15.
 */
public interface PhotoListPageView {
    void setupPagerAdapter(List<PhotoDetails> photos);
}
