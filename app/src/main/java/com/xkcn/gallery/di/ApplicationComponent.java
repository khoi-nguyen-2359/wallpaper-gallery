package com.xkcn.gallery.di;

import android.content.Context;

import com.xkcn.gallery.activity.BaseActivity;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PhotoTagRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.fragment.BaseFragment;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.usecase.PhotoDetailsUsecase;

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
    PhotoDetailsRepository photoDetailsReposiroty();
    PreferenceRepository preferenceReposiroty();
    PhotoTagRepository photoTagRepository();
}
