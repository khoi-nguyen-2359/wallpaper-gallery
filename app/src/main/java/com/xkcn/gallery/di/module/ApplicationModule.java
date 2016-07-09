package com.xkcn.gallery.di.module;

import android.content.Context;

import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.data.DbHelper;
import com.xkcn.gallery.data.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.repo.PhotoDetailsSqliteRepository;
import com.xkcn.gallery.data.repo.PhotoTagRepository;
import com.xkcn.gallery.data.repo.PhotoTagSqliteRepository;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.data.repo.PreferenceRepositoryImpl;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

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
  PhotoFileManager providePhotoFileManager() {
    return new PhotoFileManager(baseApp);
  }

  @Provides
  @Singleton
  PhotoTagRepository providePhotoTagRepository() {
    return new PhotoTagSqliteRepository(dbHelper);
  }

  @Provides
  @Singleton
  Scheduler provideRxIoScheduler() {
    return Schedulers.io();
  }
}
