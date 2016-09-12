package com.xkcn.gallery.manager;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface LocalConfigManager {
	long getLastPhotoCrawlTime();

	void setLastPhotoCrawlTime(long lastUpdate);

	long getLastCrawledPhotoId();

	void setLastCrawledPhotoId(long lastPhotoId);

	boolean hasPhotoCrawled();

	boolean hasOpenedLeftDrawer();

	void setLeftDrawerOpened(boolean b);

	int getListPagerPhotoPerPage();

	long getUpdatePeriod();

	int getLastWatchedPhotoListPage();

	void setLastWatchedPhotoListPage(int position);
}
