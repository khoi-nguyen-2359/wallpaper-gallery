package com.xkcn.crawler.data;

import android.content.SharedPreferences;

import com.xkcn.crawler.XkcnApp;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class PreferenceDataStoreImpl implements PreferenceDataStore {
    private static final String APP_PREF = "APP_PREF";
    public static final long PERIOD_UPDATE = 86400000;
    private static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";
    private static final String PREF_LAST_UPDATED_PHOTO_ID = "PREF_LAST_UPDATED_PHOTO_ID";
    private static final long INVALID_TIME = 1;
    private static final long INVALID_PHOTO_ID = -1;

    public static SharedPreferences getPref() {
        return XkcnApp.app().getSharedPreferences(APP_PREF, 0);
    }

    @Override
    public void setLastPhotoCrawlTime(long milisec) {
        getPref().edit().putLong(PREF_LAST_UPDATE, milisec).apply();
    }

    @Override
    public void setLastCrawledPhotoId(long lastPhotoId) {
        getPref().edit().putLong(PREF_LAST_UPDATED_PHOTO_ID, lastPhotoId).apply();
    }

    @Override
    public long getLastPhotoCrawlTime() {
        return getPref().getLong(PREF_LAST_UPDATE, INVALID_TIME);
    }

    @Override
    public long getLastCrawledPhotoId() {
        return getPref().getLong(PREF_LAST_UPDATED_PHOTO_ID, INVALID_PHOTO_ID);
    }

    @Override
    public boolean hasPhotoCrawled() {
        return getLastPhotoCrawlTime() != INVALID_TIME && getLastCrawledPhotoId() != INVALID_PHOTO_ID;
    }
}
