package com.xkcn.gallery.presenter;

import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.view.interfaces.SplashView;

import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashViewPresenter {
	private SplashView view;
	private RemoteConfigManager remoteConfigManager;

	public SplashViewPresenter(SplashView view, RemoteConfigManager remoteConfigManager) {
		this.view = view;
		this.remoteConfigManager = remoteConfigManager;
	}

	public void setupDefaultRemoteConfigs() {
		remoteConfigManager.setupDefaultConfigs();
	}

	public void runInitTasks() {
		remoteConfigManager.fetchRemoteConfig()
			.doOnTerminate(new Action0() {
				@Override
				public void call() {
					view.finishSplash();
				}
			})
			.subscribe(new Subscriber<Boolean>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(Boolean aBoolean) {

			}
		});
	}
}
