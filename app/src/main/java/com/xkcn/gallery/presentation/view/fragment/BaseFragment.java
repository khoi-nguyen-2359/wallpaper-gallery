package com.xkcn.gallery.presentation.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.local.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagRepository;
import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.manager.RemoteConfigManager;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Scheduler;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class BaseFragment extends Fragment {
	@Inject
	PhotoDetailsRepository photoDetailsRepository;
	@Inject
	LocalConfigManager localConfigManager;
	@Inject
	PhotoTagRepository photoTagRepository;
	@Inject
	PhotoFileManager photoFileManager;
	@Inject
	RemoteConfigManager remoteConfigManager;
	@Inject
	@Named(ApplicationModule.SCHEDULER_BACKGROUND)
	Scheduler schedulerBackground;
	@Inject
	AnalyticsCollection analyticsCollection;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationComponent().inject(this);
	}

	protected ApplicationComponent getApplicationComponent() {
		return ((BaseApp) getActivity().getApplication()).getApplicationComponent();
	}

	public boolean onBackPressed() {
		return false;
	}
}
