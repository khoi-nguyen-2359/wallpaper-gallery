package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.ApplicationModule;
import com.xkcn.gallery.di.module.SystemServiceModule;
import com.xkcn.gallery.presentation.view.activity.MainActivity;
import com.xkcn.gallery.presentation.navigator.Navigator;

import dagger.Subcomponent;

@Subcomponent(modules = {ApplicationModule.class, SystemServiceModule.class})
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);

	<T extends Navigator> void inject(T navigator);
}