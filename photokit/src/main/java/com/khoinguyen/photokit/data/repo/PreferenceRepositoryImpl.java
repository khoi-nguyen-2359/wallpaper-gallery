package com.khoinguyen.photokit.data.repo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class PreferenceRepositoryImpl implements PreferenceRepository {
    private static final String APP_PREF = "APP_PREF";
    private static final long PERIOD_UPDATE = 86400000;
    private static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";
    private static final String PREF_LAST_UPDATED_PHOTO_ID = "PREF_LAST_UPDATED_PHOTO_ID";
    private static final long INVALID_TIME = 1;
    private static final long INVALID_PHOTO_ID = -1;
    private static final String PREF_HAS_OPENED_LEFT_DRAWER = "PREF_HAS_OPENED_LEFT_DRAWER";
    private static final String PREF_LAST_WATCHED_PHOTO_LIST_PAGE = "PREF_LAST_WATCHED_PHOTO_LIST_PAGE";
    public static final int DEF_LISTING_PHOTO_PER_PAGE = 100;

    private SharedPreferences sharedPreferences;

    public PreferenceRepositoryImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREF, 0);
    }

    private SharedPreferences getPref() {
        return sharedPreferences;
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

    @Override
    public boolean hasOpenedLeftDrawer() {
        return getPref().getBoolean(PREF_HAS_OPENED_LEFT_DRAWER, false);
    }

    @Override
    public void setLeftDrawerOpened(boolean b) {
        getPref().edit().putBoolean(PREF_HAS_OPENED_LEFT_DRAWER, b).apply();
    }

    @Override
    public int getListPagerPhotoPerPage() {
        return DEF_LISTING_PHOTO_PER_PAGE;
    }

    @Override
    public long getUpdatePeriod() {
        return PERIOD_UPDATE;
    }

    @Override
    public void setLastWatchedPhotoListPage(int position) {
        getPref().edit().putInt(PREF_LAST_WATCHED_PHOTO_LIST_PAGE, position).apply();
    }

    @Override
    public int getLastWatchedPhotoListPage() {
        return getPref().getInt(PREF_LAST_WATCHED_PHOTO_LIST_PAGE, 0);
    }

}
