package com.xkcn.gallery.di;

import com.khoinguyen.photokit.adapter.PhotoListingPagerAdapter;
import com.khoinguyen.photokit.presenter.PhotoListingViewPagerPresenter;
import com.khoinguyen.photokit.presenter.PhotoListingViewPresenter;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PhotoModule.class})
public interface PhotoComponent extends ActivityComponent {
    void inject(PhotoListingPagerAdapter photoListingPagerAdapter);
    void inject(PhotoListingViewPagerPresenter photoListingViewPagerPresenter);
    void inject(PhotoListingViewPresenter detailsPagerPresenter);
}