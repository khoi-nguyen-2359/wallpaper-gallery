package com.xkcn.crawler;

import android.app.Application;

import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;

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
        app = this;

        XkcnFrescoImageLoader.init(this);
    }
}
