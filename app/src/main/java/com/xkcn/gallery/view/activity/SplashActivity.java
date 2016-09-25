package com.xkcn.gallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.presenter.SplashViewPresenter;
import com.xkcn.gallery.view.interfaces.SplashView;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashActivity extends BaseActivity implements SplashView {
	private SplashViewPresenter presenter;

	@Inject
	RemoteConfigManager remoteConfigManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getApplicationComponent().inject(this);

		initData();
		presenter.setupDefaultRemoteConfigs();
		presenter.runInitTasks();
	}

	private void initData() {
		presenter = new SplashViewPresenter(this, remoteConfigManager);
	}

	@Override
	public void finishSplash() {
		Intent intent = new Intent(this, MainActivityImpl.class);
		startActivity(intent);

		finish();
	}
}
