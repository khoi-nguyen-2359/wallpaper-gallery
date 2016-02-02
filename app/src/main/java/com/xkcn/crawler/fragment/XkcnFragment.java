package com.xkcn.crawler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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
public abstract class XkcnFragment extends Fragment {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((XkcnApp) getActivity().getApplication()).getApplicationComponent();
    }
}
