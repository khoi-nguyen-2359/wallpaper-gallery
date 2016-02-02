package com.xkcn.crawler.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.data.PhotoDetailsRepository;
import com.xkcn.crawler.data.PhotoTagRepository;
import com.xkcn.crawler.data.PreferenceRepository;
import com.xkcn.crawler.di.ApplicationComponent;
import com.xkcn.crawler.imageloader.XkcnImageLoader;
import com.xkcn.crawler.usecase.PhotoDownloader;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class XkcnActivity extends AppCompatActivity {
    @Inject
    PhotoDetailsRepository photoDetailsRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    PhotoTagRepository photoTagRepository;
    @Inject
    PhotoDownloader photoDownloader;
    @Inject
    XkcnImageLoader xkcnImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((XkcnApp) getApplication()).getApplicationComponent();
    }
}
