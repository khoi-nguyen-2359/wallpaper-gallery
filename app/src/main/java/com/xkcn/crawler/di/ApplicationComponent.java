package com.xkcn.crawler.di;

import android.content.Context;

import com.xkcn.crawler.activity.XkcnActivity;
import com.xkcn.crawler.data.PhotoDetailsRepository;
import com.xkcn.crawler.data.PhotoTagRepository;
import com.xkcn.crawler.data.PreferenceRepository;
import com.xkcn.crawler.fragment.XkcnFragment;
import com.xkcn.crawler.service.UpdateService;

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
