package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.view.interfaces.SplashView;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashViewPresenter {
	private SplashView view;
	private PreferenceRepository prefDataStore;

	public SplashViewPresenter(SplashView view, PreferenceRepository prefDataStore) {
		this.view = view;
		this.prefDataStore = prefDataStore;
	}
}
