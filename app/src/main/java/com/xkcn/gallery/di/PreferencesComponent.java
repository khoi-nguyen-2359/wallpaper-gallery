package com.xkcn.gallery.di;

import com.xkcn.gallery.activity.BaseActivity;
import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PreferencesModule.class})
public interface PreferencesComponent extends ActivityComponent {
    void inject(PhotoListingViewPagerPresenter photoListingViewPagerPresenter);
}