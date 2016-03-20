package com.xkcn.gallery.di;

import android.content.Context;

import com.xkcn.gallery.XkcnApp;
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
    private final XkcnApp xkcnApp;

    public ApplicationModule(XkcnApp app) {
        this.xkcnApp = app;
    }

    @Provides @Singleton
    Context provideContext() {
        return this.xkcnApp;
    }

    @Provides @Singleton
    PhotoDetailsRepository providePhotoDetailsDataStore() {
        return new PhotoDetailsSqliteRepository(xkcnApp);
    }

    @Provides
    @Singleton
    PreferenceRepository providePreferenceDataStore() {
        return new PreferenceRepositoryImpl(xkcnApp);
    }

    @Provides
    @Singleton
    PhotoDownloader providePhotoDownloader() {
        return new PhotoDownloader(xkcnApp);
    }

    @Provides
    @Singleton
    PhotoTagRepository providePhotoTagRepository() {
        return new PhotoTagSqliteRepository(xkcnApp);
    }
}
