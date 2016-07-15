package com.xkcn.gallery.data.repo;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface PreferenceRepository {
  void setLastPhotoCrawlTime(long lastUpdate);

  void setLastCrawledPhotoId(long lastPhotoId);

  long getLastPhotoCrawlTime();

  long getLastCrawledPhotoId();

  boolean hasPhotoCrawled();

  boolean hasOpenedLeftDrawer();

  void setLeftDrawerOpened(boolean b);

  int getListPagerPhotoPerPage();

  long getUpdatePeriod();

  void setLastWatchedPhotoListPage(int position);

  int getLastWatchedPhotoListPage();
}
