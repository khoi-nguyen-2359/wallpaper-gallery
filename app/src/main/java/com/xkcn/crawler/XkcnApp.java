package com.xkcn.crawler;

import android.app.Application;

import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;
import com.crashlytics.android.Crashlytics;
import com.xkcn.crawler.service.UpdateService;

import io.fabric.sdk.android.Fabric;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class XkcnApp extends Application {
    private static XkcnApp app;

    public static XkcnApp app() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        app = this;

        XkcnFrescoImageLoader.init(this);
    }
}
