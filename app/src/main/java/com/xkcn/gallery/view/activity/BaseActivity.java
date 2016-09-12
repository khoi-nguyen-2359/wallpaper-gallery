package com.xkcn.gallery.view.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.R;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.local.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagRepository;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.view.fragment.BaseFragment;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
	@Inject
	PhotoDetailsRepository photoDetailsRepository;
	@Inject
	LocalConfigManager localConfigManager;
	@Inject
	PhotoTagRepository photoTagRepository;
	@Inject
	PhotoFileManager photoFileManager;
	@Inject
	PhotoListingUsecase photoListingUsecase;
	@Inject
	PreferencesUsecase preferencesUsecase;
	@Inject
	AnalyticsCollection analyticsCollection;
	@Inject
	RemoteConfigManager remoteConfigManager;

	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getApplicationComponent().inject(this);
	}

	protected ApplicationComponent getApplicationComponent() {
		return ((BaseApp) getApplication()).getApplicationComponent();
	}

	public NotificationManager getNotificationManager() {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}

		return notificationManager;
	}

	@Override
	public void onBackPressed() {
		if (!handleCurrentFragmentBackPressed()) {
			super.onBackPressed();
		}
	}

	private boolean handleCurrentFragmentBackPressed() {
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f == null || !(f instanceof BaseFragment)) {
			return false;
		}

		BaseFragment baseF = (BaseFragment) f;
		return baseF.onBackPressed();
	}
}
