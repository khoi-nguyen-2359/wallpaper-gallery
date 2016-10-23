package com.xkcn.gallery;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.khoinguyen.photoviewerkit.impl.PhotoViewerKit;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.di.component.DaggerApplicationComponent;
import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.di.module.SystemServiceModule;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class BaseApp extends Application {
	private ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		Realm.init(this);
		initInjector();
		PhotoViewerKit.init(this);
		JodaTimeAndroid.init(this);
	}

	private void initInjector() {
		applicationComponent = DaggerApplicationComponent.builder()
			.applicationModule(new ApplicationModule(this))
			.systemServiceModule(new SystemServiceModule(this))
			.build();
	}

	public ApplicationComponent getApplicationComponent() {
		return applicationComponent;
	}
}
