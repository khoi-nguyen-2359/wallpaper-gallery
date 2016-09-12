package com.xkcn.gallery.di.module;

import android.app.NotificationManager;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by khoinguyen on 9/12/16.
 */
@Module
public class SystemServiceModule {
	private Context appContext;

	public SystemServiceModule(Context appContext) {
		this.appContext = appContext;
	}

	@Provides
	NotificationManager providesNotificationManager() {
		return (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
}
