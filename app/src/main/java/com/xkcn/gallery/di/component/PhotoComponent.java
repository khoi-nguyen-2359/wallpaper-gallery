package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.ActivityModule;
import com.xkcn.gallery.di.scope.PerActivity;
import com.xkcn.gallery.di.module.PhotoModule;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PhotoModule.class})
public interface PhotoComponent extends ActivityComponent {
}