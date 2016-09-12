package com.xkcn.gallery.di.module;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.analytics.GoogleAnalytics;
import com.xkcn.gallery.data.local.DbHelper;
import com.xkcn.gallery.data.local.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.local.repo.PhotoDetailsSqliteRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagSqliteRepository;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.manager.impl.LocalConfigManagerImpl;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.manager.impl.RemoteConfigManagerImpl;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;

import java.util.regex.Pattern;

import javax.inject.Named;
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
	private static final String PATTERN_SQLITE_LIMIT_CLAUSE = "PATTERN_SQLITE_LIMIT_CLAUSE";
	public static final String SCHEDULER_BACKGROUND = "SCHEDULER_BACKGROUND";

	private final BaseApp baseApp;

	public ApplicationModule(BaseApp app) {
		this.baseApp = app;
	}

	@Provides
	@Singleton
	@Named(SCHEDULER_BACKGROUND)
	Scheduler providesBackgroundScheduler() {
		return Schedulers.io();
	}

	@Provides
	@Singleton
	public DbHelper providesDbHelper() {
		return new DbHelper(baseApp);
	}

	@Provides
	@Singleton
	PreferencesUsecase providePreferencesUsecase(LocalConfigManager localConfigManager) {
		return new PreferencesUsecase(localConfigManager);
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
	@Named(PATTERN_SQLITE_LIMIT_CLAUSE)
	public Pattern provideSqliteLimitClausePattern() {
		return Pattern.compile("\\blimit *\\d+ *(, *\\d+)?\\b");
	}

	@Provides
	@Singleton
	PhotoDetailsRepository providePhotoDetailsDataStore(@Named(PATTERN_SQLITE_LIMIT_CLAUSE) Pattern limitClauseDetector, DbHelper dbHelper) {
		return new PhotoDetailsSqliteRepository(limitClauseDetector, dbHelper);
	}

	@Provides
	@Singleton
	LocalConfigManager provideLocalConfigManager() {
		return new LocalConfigManagerImpl(baseApp);
	}

	@Provides
	@Singleton
	PhotoFileManager providePhotoFileManager() {
		return new PhotoFileManager(baseApp);
	}

	@Provides
	@Singleton
	PhotoTagRepository providePhotoTagRepository(DbHelper dbHelper) {
		return new PhotoTagSqliteRepository(dbHelper);
	}

	@Provides
	@Singleton
	Scheduler provideRxIoScheduler() {
		return Schedulers.io();
	}

	@Provides
	@Singleton
	AnalyticsCollection provideAnalyticsCollection() {
		AnalyticsCollection collection = new AnalyticsCollection();
		collection.addTracker(new GoogleAnalytics(baseApp));
		return collection;
	}

	@Provides
	@Singleton
	public RemoteConfigManager provideRemoteConfigManager() {
		return new RemoteConfigManagerImpl(baseApp.getResources(), FirebaseRemoteConfig.getInstance(), new Gson());
	}
}
