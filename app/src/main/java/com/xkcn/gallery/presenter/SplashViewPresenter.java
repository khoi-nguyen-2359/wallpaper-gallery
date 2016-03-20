package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.PreferenceRepository;
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

    public void checkToCrawlPhoto() {
        if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
            view.startActionUpdate();
        }
    }
}
