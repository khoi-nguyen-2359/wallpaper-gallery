package com.xkcn.crawler;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fantageek.toolkit.util.L;
import com.xkcn.crawler.di.ApplicationComponent;
import com.xkcn.crawler.di.ApplicationModule;
import com.xkcn.crawler.di.DaggerApplicationComponent;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class XkcnApp extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        initInjector();
        Fabric.with(this, new Crashlytics());
        L.setClassLoggable(BuildConfig.LOGGABLE);
    }

    private void initInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
