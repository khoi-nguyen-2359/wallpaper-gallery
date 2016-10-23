package com.xkcn.gallery.manager.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.model.LastWatchedPhotoPage;

import io.realm.Realm;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class LocalConfigManagerImpl implements LocalConfigManager {
	private static final String APP_PREF = "APP_PREF";
	private static final long PERIOD_UPDATE = 86400000;
	private static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";
	private static final String PREF_LAST_UPDATED_PHOTO_ID = "PREF_LAST_UPDATED_PHOTO_ID";
	private static final long INVALID_TIME = 1;
	private static final long INVALID_PHOTO_ID = -1;
	private static final String PREF_HAS_OPENED_LEFT_DRAWER = "PREF_HAS_OPENED_LEFT_DRAWER";
	private static final String PREF_LAST_WATCHED_PHOTO_LIST_PAGE = "PREF_LAST_WATCHED_PHOTO_LIST_PAGE";
	private SharedPreferences sharedPreferences;

	private Realm realmLocalConfig;

	public LocalConfigManagerImpl(Context context, Realm realmLocalConfig) {
		sharedPreferences = context.getSharedPreferences(APP_PREF, 0);
		this.realmLocalConfig = realmLocalConfig;
	}

	private SharedPreferences getPref() {
		return sharedPreferences;
	}

	@Override
	public long getLastPhotoCrawlTime() {
		return getPref().getLong(PREF_LAST_UPDATE, INVALID_TIME);
	}

	@Override
	public void setLastPhotoCrawlTime(long milisec) {
		getPref().edit().putLong(PREF_LAST_UPDATE, milisec).apply();
	}

	@Override
	public long getLastCrawledPhotoId() {
		return getPref().getLong(PREF_LAST_UPDATED_PHOTO_ID, INVALID_PHOTO_ID);
	}

	@Override
	public void setLastCrawledPhotoId(long lastPhotoId) {
		getPref().edit().putLong(PREF_LAST_UPDATED_PHOTO_ID, lastPhotoId).apply();
	}

	@Override
	public boolean hasPhotoCrawled() {
		return getLastPhotoCrawlTime() != INVALID_TIME && getLastCrawledPhotoId() != INVALID_PHOTO_ID;
	}

	@Override
	public boolean hasOpenedLeftDrawer() {
		return getPref().getBoolean(PREF_HAS_OPENED_LEFT_DRAWER, false);
	}

	@Override
	public void setLeftDrawerOpened(boolean b) {
		getPref().edit().putBoolean(PREF_HAS_OPENED_LEFT_DRAWER, b).apply();
	}

	@Override
	public long getUpdatePeriod() {
		return PERIOD_UPDATE;
	}

	@Override
	public int getListingPhotoPerPage() {
		return 100;
	}

	@Override
	public int getLastWatchedPhotoListingItem(String collectionName) {
		LastWatchedPhotoPage lastPage = realmLocalConfig.where(LastWatchedPhotoPage.class)
			.equalTo(LastWatchedPhotoPage.FIELD_COLLECTION_NAME, collectionName)
			.findFirst();

		return lastPage == null ? -1 : lastPage.getLastWatchedFirstVisibleIndex();
	}

	@Override
	public void setLastWatchedPhotoListingItem(String collectionName, int firstVisibleItem) {
		LastWatchedPhotoPage lastPage = new LastWatchedPhotoPage();
		lastPage.setCollectionName(collectionName);
		lastPage.setLastWatchedFirstVisibleIndex(firstVisibleItem);
		L.get("listing").d("set last watched %s %d", collectionName, firstVisibleItem);
		realmLocalConfig.executeTransaction(realm -> realm.copyToRealmOrUpdate(lastPage));
	}
}
