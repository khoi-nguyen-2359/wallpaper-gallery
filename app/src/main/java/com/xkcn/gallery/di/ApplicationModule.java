package com.xkcn.gallery.di;

import android.content.Context;

import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PhotoDetailsSqliteRepository;
import com.xkcn.gallery.data.PhotoTagRepository;
import com.xkcn.gallery.data.PhotoTagSqliteRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.data.PreferenceRepositoryImpl;
import com.xkcn.gallery.imageloader.PhotoDownloader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by khoinguyen on 1/27/16.
 */
@Module
public class ApplicationModule {
    private final BaseApp baseApp;

    public ApplicationModule(BaseApp app) {
        this.baseApp = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return this.baseApp;
    }

    @Provides
    @Singleton
    PhotoDetailsRepository providePhotoDetailsDataStore() {
        return new PhotoDetailsSqliteRepository(baseApp);
    }

    @Provides
    @Singleton
    PreferenceRepository providePreferenceDataStore() {
        return new PreferenceRepositoryImpl(baseApp);
    }

    @Provides
    @Singleton
    PhotoDownloader providePhotoDownloader() {
        return new PhotoDownloader(baseApp);
    }

    @Provides
    @Singleton
    PhotoTagRepository providePhotoTagRepository() {
        return new PhotoTagSqliteRepository(baseApp);
    }
}
