package com.xkcn.gallery.di;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PreferencesModule.class})
public interface PreferencesComponent extends ActivityComponent {
}