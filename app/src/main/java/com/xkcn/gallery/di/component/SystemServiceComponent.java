package com.xkcn.gallery.di.component;

import com.xkcn.gallery.di.module.SystemServiceModule;
import com.xkcn.gallery.presentation.view.fragment.BaseFragment;
import com.xkcn.gallery.presentation.view.fragment.PhotoCollectionFragment;

import dagger.Subcomponent;

/**
 * Created by khoinguyen on 9/12/16.
 */
@Subcomponent(modules = SystemServiceModule.class)
public interface SystemServiceComponent {
	void inject(PhotoCollectionFragment fragment);
	void inject(BaseFragment fragment);
}
