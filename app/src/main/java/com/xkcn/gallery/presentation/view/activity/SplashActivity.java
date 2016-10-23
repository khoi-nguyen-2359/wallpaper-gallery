package com.xkcn.gallery.presentation.view.activity;

import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.presentation.viewmodel.SplashViewModel;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashActivity extends BaseActivity {
	private SplashViewModel splashViewModel;

	@Inject
	RemoteConfigManager remoteConfigManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getApplicationComponent().inject(this);

		initData();
		splashViewModel.setupDefaultRemoteConfigs();
		splashViewModel.runInitTasks();
	}

	private void initData() {
		splashViewModel = new SplashViewModel(remoteConfigManager);
		splashViewModel.obsInitTaskResult.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				finishSplash();
			}
		});
		splashViewModel.obsError.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showErrorDialog(splashViewModel.obsError.get());
			}
		});
	}

	public void finishSplash() {
		Intent intent = new Intent(this, MainActivityImpl.class);
		startActivity(intent);

		finish();
	}
}
