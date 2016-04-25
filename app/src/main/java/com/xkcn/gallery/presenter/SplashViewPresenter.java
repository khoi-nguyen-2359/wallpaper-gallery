package com.xkcn.gallery.presenter;

import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.xkcn.gallery.view.SplashView;

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
