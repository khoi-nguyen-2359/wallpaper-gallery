package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.di.module.SystemServiceModule;
import com.xkcn.gallery.presenter.PhotoCollectionViewModel;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.view.activity.BaseActivity;
import com.xkcn.gallery.view.activity.SplashActivity;
import com.xkcn.gallery.view.fragment.BaseFragment;
import com.xkcn.gallery.view.fragment.PhotoCollectionFragment;

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
