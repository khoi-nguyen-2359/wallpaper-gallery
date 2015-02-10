package com.xkcn.crawler.photoactions;

import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;

/**
 * Created by khoinguyen on 2/11/15.
 */
public interface PhotoDownloadEventSubscriber {
    void onEventMainThread(PhotoDownloadedEvent e);
    void onEventMainThread(PhotoDownloadFailedEvent e);
}
