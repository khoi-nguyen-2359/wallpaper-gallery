package com.xkcn.gallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.khoinguyen.photokit.usecase.PhotoListingUsecase;
import com.khoinguyen.photokit.usecase.PreferencesUsecase;
import com.xkcn.gallery.BaseApp;
import com.khoinguyen.photokit.data.repo.PhotoDetailsRepository;
import com.khoinguyen.photokit.data.repo.PhotoTagRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.xkcn.gallery.di.ActivityModule;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.di.DaggerPhotoComponent;
import com.xkcn.gallery.di.DaggerPreferencesComponent;
import com.xkcn.gallery.di.PhotoComponent;
import com.xkcn.gallery.di.PreferencesComponent;
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
