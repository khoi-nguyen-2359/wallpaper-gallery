package com.xkcn.gallery.di;

import android.content.Context;

import com.khoinguyen.photokit.usecase.PhotoListingUsecase;
import com.khoinguyen.photokit.usecase.PreferencesUsecase;
import com.xkcn.gallery.activity.BaseActivity;
import com.khoinguyen.photokit.data.repo.PhotoDetailsRepository;
import com.khoinguyen.photokit.data.repo.PhotoTagRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.xkcn.gallery.data.DbHelper;
import com.xkcn.gallery.fragment.BaseFragment;
import com.xkcn.gallery.service.UpdateService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by khoinguyen on 1/27/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity activity);
    void inject(BaseFragment baseFragment);
    void inject(UpdateService updateService);

    Context context();
    PhotoDetailsRepository photoDetailsRepository();
    PreferenceRepository preferenceRepository();
    PhotoTagRepository photoTagRepository();
    PhotoListingUsecase photoListingUsecase();
    PreferencesUsecase preferencesUsecase();
}
