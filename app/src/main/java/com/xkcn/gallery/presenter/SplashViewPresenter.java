package com.xkcn.gallery.presenter;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.view.interfaces.SplashView;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashViewPresenter {
	private SplashView view;
	private RemoteConfigManager remoteConfigManager;
	private Task<Void> fetchRemoteConfigsTask;
	private OnCompleteListener<Void> onFetchRemoteConfigComplete;

	private boolean areInitTasksCompleted;

	public SplashViewPresenter(SplashView view, RemoteConfigManager remoteConfigManager) {
		this.view = view;
		this.remoteConfigManager = remoteConfigManager;
	}

	public void setupDefaultRemoteConfigs() {
		remoteConfigManager.setupDefaultConfigs();
	}

	public void runInitTasks() {
		Task taskFetchRemoteConfigs = getFetchRemoteConfigsTask();
		taskFetchRemoteConfigs
			.continueWith(getTaskInitContinuation());
	}

	private Continuation getTaskInitContinuation() {
		return new Continuation() {
			@Override
			public Object then(@NonNull Task task) throws Exception {
				L.get().d("init tasks completed");
				areInitTasksCompleted = true;
				checkToFinishSplashView();
				// dont care return value
				return null;
			}
		};
	}

	private void checkToFinishSplashView() {
		if (areInitTasksCompleted) {
			view.finishSplash();
		}
	}

	private Task getFetchRemoteConfigsTask() {
		final FirebaseRemoteConfig firebaseConfigs = FirebaseRemoteConfig.getInstance();
		fetchRemoteConfigsTask = firebaseConfigs.fetch();
		onFetchRemoteConfigComplete = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				L.get().d("onComplete");
				firebaseConfigs.activateFetched();
			}
		};

		fetchRemoteConfigsTask.addOnCompleteListener(onFetchRemoteConfigComplete);

		return fetchRemoteConfigsTask;
	}
}
