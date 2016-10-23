package com.xkcn.gallery.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.xkcn.gallery.manager.RemoteConfigManager;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashViewModel {
	public ObservableBoolean obsInitTaskResult = new ObservableBoolean();
	public ObservableField<Throwable> obsError = new ObservableField<>();

	private RemoteConfigManager remoteConfigManager;

	public SplashViewModel(RemoteConfigManager remoteConfigManager) {
		this.remoteConfigManager = remoteConfigManager;
	}

	public void setupDefaultRemoteConfigs() {
		remoteConfigManager.setupDefaultConfigs();
	}

	public void runInitTasks() {
		remoteConfigManager.fetchRemoteConfig()
			.subscribe(
				result -> obsInitTaskResult.set(result),
				error -> obsError.set(error)
			);
	}
}
