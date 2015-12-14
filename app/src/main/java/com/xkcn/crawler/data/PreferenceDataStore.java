package com.xkcn.crawler.data;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface PreferenceDataStore {
    void setLastPhotoCrawlTime(long lastUpdate);
    void setLastCrawledPhotoId(long lastPhotoId);
    long getLastPhotoCrawlTime();
    long getLastCrawledPhotoId();
    boolean hasPhotoCrawled();
    boolean hasOpenedLeftDrawer();

    void setLeftDrawerOpened(boolean b);
    int getListPagerPhotoPerPage();

    long getUpdatePeriod();
}
