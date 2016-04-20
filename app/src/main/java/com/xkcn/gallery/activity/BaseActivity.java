package com.xkcn.gallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PhotoTagRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.di.ActivityModule;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.di.DaggerPhotoComponent;
import com.xkcn.gallery.di.DaggerPreferencesComponent;
import com.xkcn.gallery.di.PhotoComponent;
import com.xkcn.gallery.di.PreferencesComponent;
import com.xkcn.gallery.imageloader.PhotoDownloader;
import com.xkcn.gallery.usecase.PhotoDetailsUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;

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

    protected PhotoComponent photoComponent;
    protected PreferencesComponent preferencesComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjectors();

        getApplicationComponent().inject(this);
    }

    protected void initInjectors() {
        photoComponent = DaggerPhotoComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        preferencesComponent = DaggerPreferencesComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((BaseApp) getApplication()).getApplicationComponent();
    }

    protected PreferencesComponent getPreferencesComponent() {
        return preferencesComponent;
    }

    protected PhotoComponent getPhotoComponent() {
        return photoComponent;
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }
}
