package com.xkcn.gallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.gallery.data.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.repo.PhotoTagRepository;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoDownloader;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Inject
    PhotoDetailsRepository photoDetailsRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    PhotoTagRepository photoTagRepository;
    @Inject
    PhotoDownloader photoDownloader;
    @Inject
    PhotoListingUsecase photoListingUsecase;
    @Inject
    PreferencesUsecase preferencesUsecase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((BaseApp) getApplication()).getApplicationComponent();
    }
}
