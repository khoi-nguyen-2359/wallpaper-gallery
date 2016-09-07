package com.xkcn.gallery.presenter;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.view.interfaces.MainView;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class MainViewPresenter {
	@Inject
	PhotoFileManager photoFileManager;
	@Inject
	PreferenceRepository prefDataStore;
	@Inject
	Scheduler rxIoScheduler;
	@Inject
	AnalyticsCollection analyticsCollection;
	private MainView view;
	/**
	 * MainView has one task that is requisite and block the UI
	 */
	private Subscription blockingTask;

	public MainViewPresenter(MainView view) {
		this.view = view;
	}

	public void loadWallpaperSetting(final PhotoDetails photoDetails) {
		view.showProgressLoading(R.string.photo_gallery_downloading_message);

		blockingTask = photoFileManager.getPhotoFileObservable(photoDetails, rxIoScheduler)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Subscriber<Float>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
					view.showWallpaperChooser(photoDetails);
				}

				@Override
				public void onError(Throwable e) {
					view.hideLoading();
					view.showToast(e.getMessage());
				}

				@Override
				public void onNext(Float progress) {
					L.get().d("load wallpaper onNext %f", progress);
					view.updateProgressLoading((int) (progress * 100));
				}
			});
	}

	public void checkToCrawlPhoto() {
		if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
			view.startActionUpdate();
		}
	}

	public void downloadPhoto(final PhotoDetails photoDetails) {
		if (photoDetails == null) {
			return;
		}

		photoFileManager.getPhotoFileObservable(photoDetails, rxIoScheduler)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Subscriber<Float>() {
				@Override
				public void onCompleted() {
					view.showDownloadComplete(photoDetails);
				}

				@Override
				public void onError(Throwable e) {
					view.showDownloadError(photoDetails, e.getMessage());
				}

				@Override
				public void onNext(Float progress) {
					L.get().d("download onNext %f", progress);
					view.updateDownloadProgress(photoDetails, progress);
				}
			});
	}

	public void sharePhoto(final PhotoDetails photoDetails) {
		if (photoDetails == null) {
			return;
		}

		view.showProgressLoading(R.string.photo_gallery_downloading_message);
		blockingTask = photoFileManager.getPhotoFileObservable(photoDetails, rxIoScheduler)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Subscriber<Float>() {
				@Override
				public void onCompleted() {
					view.hideLoading();
					view.showSharingChooser(photoDetails);
				}

				@Override
				public void onError(Throwable e) {
					view.hideLoading();
					view.showToast(e.getMessage());
				}

				@Override
				public void onNext(Float progress) {
					L.get().d("share onNext %f", progress);
					view.updateProgressLoading((int) (progress * 100));
				}
			});
	}

	public void cancelBlockingTask() {
		if (blockingTask != null) {
			L.get().d("cancel blocking task");
			blockingTask.unsubscribe();
			blockingTask = null;
		}

		view.hideLoading();
	}
}
