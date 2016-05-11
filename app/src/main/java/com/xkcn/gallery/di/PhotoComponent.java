package com.xkcn.gallery.di;

import com.xkcn.gallery.presenter.PhotoListingViewPresenter;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PhotoModule.class})
public interface PhotoComponent extends ActivityComponent {
  void inject(PhotoListingViewPresenter detailsPagerPresenter);
}