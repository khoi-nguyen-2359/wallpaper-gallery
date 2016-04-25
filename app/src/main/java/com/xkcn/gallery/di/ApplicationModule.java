package com.xkcn.gallery.di;

import android.content.Context;

import com.khoinguyen.photokit.usecase.PhotoListingUsecase;
import com.khoinguyen.photokit.usecase.PreferencesUsecase;
import com.xkcn.gallery.BaseApp;
import com.khoinguyen.photokit.data.repo.PhotoDetailsRepository;
import com.khoinguyen.photokit.data.repo.PhotoDetailsSqliteRepository;
import com.khoinguyen.photokit.data.repo.PhotoTagRepository;
import com.khoinguyen.photokit.data.repo.PhotoTagSqliteRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepositoryImpl;
import com.xkcn.gallery.data.DbHelper;
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
    private final DbHelper dbHelper;

    public ApplicationModule(BaseApp app) {
        this.baseApp = app;
        dbHelper = new DbHelper(app);
    }

    @Provides
    @Singleton
    PreferencesUsecase providePreferencesUsecase(PreferenceRepository preferenceRepository) {
        return new PreferencesUsecase(preferenceRepository);
    }

    @Provides
    @Singleton
    PhotoListingUsecase providePhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository) {
        return new PhotoListingUsecase(photoDetailsRepository);
    }

    @Provides
    @Singleton
    Context provideContext() {
        return this.baseApp;
    }

    @Provides
    @Singleton
    PhotoDetailsRepository providePhotoDetailsDataStore() {
        return new PhotoDetailsSqliteRepository(dbHelper);
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
        return new PhotoTagSqliteRepository(dbHelper);
    }
}
