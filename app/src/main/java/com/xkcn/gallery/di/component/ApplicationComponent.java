package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.di.module.SystemServiceModule;
import com.xkcn.gallery.presentation.viewmodel.PhotoCollectionViewModel;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.presentation.view.activity.BaseActivity;
import com.xkcn.gallery.presentation.view.activity.SplashActivity;
import com.xkcn.gallery.presentation.view.fragment.BaseFragment;
import com.xkcn.gallery.presentation.view.fragment.PhotoCollectionFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by khoinguyen on 1/27/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, SystemServiceModule.class})
public interface ApplicationComponent {
	void inject(SplashActivity activity);

	void inject(BaseActivity activity);

	void inject(BaseFragment baseFragment);

	void inject(UpdateService updateService);

	void inject(PhotoCollectionViewModel photoCollectionViewModel);

	void inject(PhotoCollectionFragment photoCollectionFragment);
}
