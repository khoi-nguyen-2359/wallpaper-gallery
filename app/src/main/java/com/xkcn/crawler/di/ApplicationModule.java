package com.xkcn.crawler.di;

import android.content.Context;

import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.data.PhotoDetailsRepository;
import com.xkcn.crawler.data.PhotoDetailsSqliteRepository;
import com.xkcn.crawler.data.PhotoTagRepository;
import com.xkcn.crawler.data.PhotoTagSqliteRepository;
import com.xkcn.crawler.data.PreferenceRepository;
import com.xkcn.crawler.data.PreferenceRepositoryImpl;
import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;
import com.xkcn.crawler.imageloader.XkcnImageLoader;
import com.xkcn.crawler.usecase.PhotoDownloader;

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
    XkcnImageLoader provideXkcnImageLoader() {
        return new XkcnFrescoImageLoader(xkcnApp);
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
