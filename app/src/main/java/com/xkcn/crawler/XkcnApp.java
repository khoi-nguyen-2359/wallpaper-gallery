package com.xkcn.crawler;

import android.app.Application;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class XkcnApp extends Application {
    public static XkcnApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
