package com.xkcn.crawler.util;

import android.content.SharedPreferences;

import com.xkcn.crawler.XkcnApp;

/**
 * Created by khoinguyen on 2/17/15.
 */
public final class P {
    public static final String APP_PREF = "APP_PREF";
    public static final long PERIOD_UPDATE = 86400000;
    public static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";
    public static final String PREF_LAST_UPDATED_PHOTO_ID = "PREF_LAST_UPDATED_PHOTO_ID";

    public static SharedPreferences get() {
        return XkcnApp.app.getSharedPreferences(APP_PREF, 0);
    }

    public static void saveLastUpdateTime(long lastUpdate) {
        get().edit().putLong(PREF_LAST_UPDATE, lastUpdate).apply();
    }

    public static long getLastUpdateTime() {
        return get().getLong(PREF_LAST_UPDATE, 0);
    }

    public static long getLastUpdatedPhotoId() {
        return get().getLong(PREF_LAST_UPDATED_PHOTO_ID, 0);
    }

    public static void saveLastUpdatedPhotoId(long lastPhotoId) {
        get().edit().putLong(PREF_LAST_UPDATED_PHOTO_ID, lastPhotoId).apply();
    }
}
