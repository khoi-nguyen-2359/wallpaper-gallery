package com.xkcn.gallery;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.khoinguyen.logging.L;
import com.squareup.okhttp.OkHttpClient;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.di.ApplicationModule;
import com.xkcn.gallery.di.DaggerApplicationComponent;
import com.crashlytics.android.Crashlytics;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class XkcnApp extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
        initInjector();
        Fabric.with(this, new Crashlytics());
        L.setClassLoggable(BuildConfig.LOGGABLE);
    }

    private void initFresco() {
        final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this, config);
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
