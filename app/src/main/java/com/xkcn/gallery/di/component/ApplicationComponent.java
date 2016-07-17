package com.xkcn.gallery.di.component;

import com.xkcn.gallery.view.activity.BaseActivity;
import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.view.fragment.BaseFragment;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
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

  void inject(MainViewPresenter mainViewPresenter);

  void inject(PhotoListingViewPresenter photoListingViewPresenter);
}
