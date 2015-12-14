package com.xkcn.crawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.presenter.SplashViewPresenter;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.view.SplashView;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashActivity extends AppCompatActivity implements SplashView {
    private SplashViewPresenter presenter;
    private PreferenceDataStore prefDataStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefDataStore = new PreferenceDataStoreImpl();
        presenter = new SplashViewPresenter(this, prefDataStore);
        presenter.checkToCrawlPhoto();
        finish();
        startActivity(new Intent(this, PhotoListPagerActivity.class));
    }

    @Override
    public void startActionUpdate() {
        UpdateService.startActionUpdate(this);
    }
}
