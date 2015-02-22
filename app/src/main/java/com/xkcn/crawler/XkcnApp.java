package com.xkcn.crawler;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class XkcnApp extends Application {
    public static XkcnApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        Picasso.with(this).setIndicatorsEnabled(BuildConfig.LOGGABLE);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
