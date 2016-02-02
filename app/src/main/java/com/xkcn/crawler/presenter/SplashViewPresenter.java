package com.xkcn.crawler.presenter;

import com.xkcn.crawler.data.PreferenceRepository;
import com.xkcn.crawler.view.SplashView;

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
