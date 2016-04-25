package com.xkcn.gallery;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.di.ApplicationModule;
import com.xkcn.gallery.di.DaggerApplicationComponent;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class BaseApp extends Application {
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
        OkHttpClient okHttpClient = new OkHttpClient();
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
