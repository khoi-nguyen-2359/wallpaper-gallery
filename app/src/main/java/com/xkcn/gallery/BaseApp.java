package com.xkcn.gallery;

import android.app.Application;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.khoinguyen.photoviewerkit.PhotoViewerKit;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.di.ApplicationModule;
import com.xkcn.gallery.di.DaggerApplicationComponent;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class BaseApp extends Application {
  private ApplicationComponent applicationComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    initInjector();
    PhotoViewerKit.init(this);
    L.setClassLoggable(BuildConfig.LOGGABLE);
    FLog.setMinimumLoggingLevel(Log.VERBOSE);
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
