package com.xkcn.gallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xkcn.gallery.XkcnApp;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PhotoTagRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoDownloader;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((XkcnApp) getApplication()).getApplicationComponent();
    }
}
