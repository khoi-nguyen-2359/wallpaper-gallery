package com.xkcn.gallery.di;

import android.content.Context;

import com.xkcn.gallery.activity.XkcnActivity;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PhotoTagRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.fragment.XkcnFragment;
import com.xkcn.gallery.service.UpdateService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by khoinguyen on 1/27/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(XkcnActivity activity);
    void inject(XkcnFragment xkcnFragment);

    void inject(UpdateService updateService);

    Context context();
    PhotoDetailsRepository photoDetailsDataStore();
    PreferenceRepository preferenceDataStore();
    PhotoTagRepository photoTagRepository();
}
