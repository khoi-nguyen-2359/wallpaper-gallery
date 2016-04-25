package com.xkcn.gallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xkcn.gallery.BaseApp;
import com.khoinguyen.photokit.data.repo.PhotoDetailsRepository;
import com.khoinguyen.photokit.data.repo.PhotoTagRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.xkcn.gallery.di.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoDownloader;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 2/1/16.
 */
public abstract class BaseFragment extends Fragment {
    @Inject
    PhotoDetailsRepository photoDetailsRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    PhotoTagRepository photoTagRepository;
    @Inject
    PhotoDownloader photoDownloader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((BaseApp) getActivity().getApplication()).getApplicationComponent();
    }
}
