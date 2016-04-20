package com.xkcn.gallery.di;

import android.app.Activity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
  Activity activity();
}