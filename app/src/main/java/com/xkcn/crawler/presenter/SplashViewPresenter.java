package com.xkcn.crawler.presenter;

import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.view.SplashView;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashViewPresenter {
    private SplashView view;
    private PreferenceDataStore prefDataStore;

    public SplashViewPresenter(SplashView view, PreferenceDataStore prefDataStore) {
        this.view = view;
        this.prefDataStore = prefDataStore;
    }

    public void checkToCrawlPhoto() {
        if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
            view.startActionUpdate();
        }
    }
}
