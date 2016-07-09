package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.ActivityModule;
import com.xkcn.gallery.di.scope.PerActivity;
import com.xkcn.gallery.di.module.PreferencesModule;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PreferencesModule.class})
public interface PreferencesComponent extends ActivityComponent {
}