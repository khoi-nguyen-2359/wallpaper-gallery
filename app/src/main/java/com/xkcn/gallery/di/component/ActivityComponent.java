package com.xkcn.gallery.di.component;

import android.app.Activity;

import com.xkcn.gallery.di.module.ActivityModule;
import com.xkcn.gallery.di.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
	Activity activity();
}