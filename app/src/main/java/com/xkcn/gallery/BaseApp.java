package com.xkcn.gallery;

import android.app.Application;

import com.khoinguyen.photoviewerkit.impl.PhotoViewerKit;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.di.component.DaggerApplicationComponent;
import com.xkcn.gallery.di.module.ApplicationModule;

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
//    FLog.setMinimumLoggingLevel(Log.VERBOSE);
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
