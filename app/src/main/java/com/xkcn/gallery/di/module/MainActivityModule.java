package com.xkcn.gallery.di.module;

import com.xkcn.gallery.view.activity.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
	private final MainActivity activity;

	public MainActivityModule(MainActivity activity) {
		this.activity = activity;
	}

	@Provides
	MainActivity mainActivity() {
		return this.activity;
	}
}