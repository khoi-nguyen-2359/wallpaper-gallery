package com.xkcn.gallery.di.module;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khoinguyen.apptemplate.concurrency.AndroidExecutors;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.BuildConfig;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.analytics.GoogleAnalytics;
import com.xkcn.gallery.data.local.DbHelper;
import com.xkcn.gallery.data.local.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.local.repo.PhotoDetailsSqliteRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagRepository;
import com.xkcn.gallery.data.local.repo.PhotoTagSqliteRepository;
import com.xkcn.gallery.data.cloud.gson_deserializer.NavigatorDeserializer;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.manager.impl.LocalConfigManagerImpl;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.manager.impl.RemoteConfigManagerImpl;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.presentation.navigator.Navigator;

import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 1/27/16.
 */
@Module
public class ApplicationModule {
	private static final String PATTERN_SQLITE_LIMIT_CLAUSE = "PATTERN_SQLITE_LIMIT_CLAUSE";
	public static final String SCHEDULER_BACKGROUND = "SCHEDULER_BACKGROUND";
	private static final String GSON_REMOTE_CONFIG = "GSON_REMOTE_CONFIG";
	private static final String EXECUTOR_PHOTO_MANAGER_CALLBACK = "EXECUTOR_PHOTO_MANAGER_CALLBACK";
	private static final String EXECUTOR_PHOTO_MANAGER_WORKER = "EXECUTOR_PHOTO_MANAGER_WORKER";

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
	PhotoListingUsecase providePhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository, @Named(EXECUTOR_PHOTO_MANAGER_WORKER) Executor exeWorker, @Named(EXECUTOR_PHOTO_MANAGER_CALLBACK) Executor exeCallback) {
		return new PhotoListingUsecase(exeWorker, exeCallback, photoDetailsRepository);
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
	Realm providesLocalConfigRealm() {
		return Realm.getDefaultInstance();
	}

	@Provides
	@Singleton
	LocalConfigManager provideLocalConfigManager(Realm realm) {
		return new LocalConfigManagerImpl(baseApp, realm);
	}

	@Provides
	@Singleton
	@Named(EXECUTOR_PHOTO_MANAGER_CALLBACK)
	Executor providesPhotoManagerFetchingExecutor() {
		return AndroidExecutors.uiThread();
	}

	@Provides
	@Singleton
	@Named(EXECUTOR_PHOTO_MANAGER_WORKER)
	Executor providesPhotoManagerWorkerExecutor() {
		return AndroidExecutors.newCachedThreadPool();
	}

	@Provides
	@Singleton
	PhotoFileManager providePhotoFileManager(@Named(EXECUTOR_PHOTO_MANAGER_WORKER) Executor executorFetchPhoto, @Named(EXECUTOR_PHOTO_MANAGER_CALLBACK) Executor exeCallback) {
		return new PhotoFileManager(baseApp, executorFetchPhoto, exeCallback);
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
	@Named(GSON_REMOTE_CONFIG)
	Gson providesRemoteConfigGson() {
		return new GsonBuilder().registerTypeAdapter(Navigator.class, new NavigatorDeserializer()).create();
	}

	@Provides
	@Singleton
	public RemoteConfigManager provideRemoteConfigManager(@Named(GSON_REMOTE_CONFIG) Gson gson) {
		FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
		FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
			.setDeveloperModeEnabled(BuildConfig.DEBUG)
			.build();
		remoteConfig.setConfigSettings(settings);
		return new RemoteConfigManagerImpl(baseApp.getResources(), remoteConfig, gson);
	}
}
