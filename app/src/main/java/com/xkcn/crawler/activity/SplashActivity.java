package com.xkcn.crawler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xkcn.crawler.data.PreferenceRepository;
import com.xkcn.crawler.presenter.SplashViewPresenter;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.view.SplashView;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class SplashActivity extends XkcnActivity implements SplashView {
    private SplashViewPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new SplashViewPresenter(this, preferenceRepository);
        presenter.checkToCrawlPhoto();
        finish();
        startActivity(new Intent(this, PhotoListPagerActivityImpl.class));
    }

    @Override
    public void startActionUpdate() {
        UpdateService.startActionUpdate(this);
    }
}
