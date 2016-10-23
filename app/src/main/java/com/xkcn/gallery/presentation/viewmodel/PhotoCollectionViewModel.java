package com.xkcn.gallery.presentation.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.cloud.model.PhotoCollection;
import com.xkcn.gallery.data.local.model.PhotoDetails;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.model.DataPage;
import com.xkcn.gallery.usecase.PhotoListingUsecase;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoCollectionViewModel {
	private PhotoFileManager photoFileManager;

	private PhotoListingUsecase photoListingUsecase;

	/**
	 * PhotoCollectionView has one task that is requisite and block the UI
	 */
	private Subscription subsPhotoLoading;

	public ObservableField<Throwable> obsError = new ObservableField<>();
	public ObservableFloat obsLoadPhotoProgress = new ObservableFloat();

	public ObservableField<DownloadPhotoProgress> obsDownloadPhotoProgress = new ObservableField<>();
	public ObservableField<DownloadPhotoError> obsDownloadError = new ObservableField<>();
	public ObservableInt obsLastWatchedPhotoIndex = new ObservableInt();

	public ObservableField<PhotoDisplayInfo> obsLoadWallpaperResult = new ObservableField<>();
	public ObservableField<PhotoDisplayInfo> obsDownloadPhotoResult = new ObservableField<>();

	public ObservableField<DataPage<PhotoDisplayInfo>> obsLastestPhotoPage = new ObservableField<>();

	private List<PhotoDetails> loadedPhotos = new ArrayList<>();

	private Action1<Throwable> actionOnError = throwable -> obsError.set(throwable);
	private Action1<? super Float> actionLoadPhotoOnNext = progress -> obsLoadPhotoProgress.set(progress);
	public ObservableField<PhotoDisplayInfo> obsSharePhotoResult = new ObservableField<>();
	private AnalyticsCollection analytics;
	private LocalConfigManager localConfigManager;
	private final int listingPhotoPerPage;

	public PhotoCollectionViewModel(PhotoFileManager photoFileManager, PhotoListingUsecase photoListingUsecase, AnalyticsCollection analytics, LocalConfigManager localConfigManager) {
		this.photoFileManager = photoFileManager;
		this.photoListingUsecase = photoListingUsecase;
		this.analytics = analytics;
		this.localConfigManager = localConfigManager;
		listingPhotoPerPage = localConfigManager.getListingPhotoPerPage();
	}

	public void loadWallpaper(PhotoDisplayInfo photoDisplayInfo) {
		PhotoDetails photoDetails = findPhoto(photoDisplayInfo);
		if (photoDetails == null) {
			return;
		}

		subsPhotoLoading = photoFileManager.getPhotoFileObservable(photoDetails)
			.subscribe(
				actionLoadPhotoOnNext,
				actionOnError,
				() -> {
					obsLoadWallpaperResult.set(photoDisplayInfo);
					analytics.trackSetWallpaperGalleryPhoto(photoDetails);
				}
			);
	}

	public void downloadPhoto(PhotoDisplayInfo photoDisplayInfo) {
		PhotoDetails photoDetails = findPhoto(photoDisplayInfo);
		if (photoDetails == null) {
			return;
		}

		photoFileManager.getPhotoFileObservable(photoDetails)
			.subscribe(
				progress -> obsDownloadPhotoProgress.set(new DownloadPhotoProgress(photoDisplayInfo, progress)),
				throwable -> obsDownloadError.set(new DownloadPhotoError(photoDisplayInfo, throwable)),
				() -> {
					obsDownloadPhotoResult.set(photoDisplayInfo);
					analytics.trackDownloadGalleryPhoto(photoDetails);
				}
			);
	}

	public void sharePhoto(final PhotoDisplayInfo photoDisplayInfo) {
		PhotoDetails photoDetails = findPhoto(photoDisplayInfo);
		if (photoDetails == null) {
			return;
		}

		subsPhotoLoading = photoFileManager.getPhotoFileObservable(photoDetails)
			.subscribe(
				actionLoadPhotoOnNext,
				actionOnError,
				() -> {
					obsSharePhotoResult.set(photoDisplayInfo);
					analytics.trackShareGalleryPhoto(photoDetails);
				}
			);
	}

	public void cancelPhotoLoadingTask() {
		if (subsPhotoLoading != null) {
			L.get().d("cancel photo loading task");
			subsPhotoLoading.unsubscribe();
			subsPhotoLoading = null;
		}
	}

	public void loadFirstPhotoPage(PhotoCollection photoCollection) {
		int perPage = listingPhotoPerPage;
		int lastWatchedIndex = localConfigManager.getLastWatchedPhotoListingItem(photoCollection.getName());
		if (lastWatchedIndex >= 0) {
			perPage = lastWatchedIndex + listingPhotoPerPage;
			obsLastWatchedPhotoIndex.set(lastWatchedIndex);
		}

		loadPhotoPage(0, photoCollection, perPage);
	}

	private void loadPhotoPage(int startIndex, final PhotoCollection collection, int perPage) {
		L.get("listing").d("collection=%s startIndex=%d perPage=%d", collection.getName(), startIndex, perPage);
		photoListingUsecase.queryPhotos(collection.getQuery(), startIndex, perPage)
			.doOnNext(photoDetailses -> {
				if (startIndex == 0) {
					loadedPhotos.clear();
				}
				loadedPhotos.addAll(photoDetailses);
			})
			.map(photoDetailses -> {
				List<PhotoDisplayInfo> displayInfos = new ArrayList<>();
				if (photoDetailses != null) {
					for (PhotoDetails photoDetails : photoDetailses) {
						displayInfos.add(photoDetails.createDisplayInfo(photoFileManager));
					}
				}

				return new DataPage<>(displayInfos, startIndex);
			})
			.subscribe(
				photoPage -> obsLastestPhotoPage.set(photoPage),
				(t) -> obsError.set(t)
			);
	}

	public void loadNextPhotoPage(PhotoCollection photoCollection) {
		DataPage<PhotoDisplayInfo> lastestPhotoPage = obsLastestPhotoPage.get();
		if (lastestPhotoPage == null) {
			return;
		}

		obsLastWatchedPhotoIndex.set(-1);
		loadPhotoPage(lastestPhotoPage.getNextStart(), photoCollection, listingPhotoPerPage);
	}

	// TODO: 10/23/16 #perf
	public
	@Nullable
	PhotoDetails findPhoto(PhotoDisplayInfo photoDisplayInfo) {
		if (photoDisplayInfo == null) {
			return null;
		}

		String photoId = photoDisplayInfo.getPhotoId();
		for (PhotoDetails photo : loadedPhotos) {
			if (photo.getIdentifierAsString().equals(photoId)) {
				return photo;
			}
		}

		return null;
	}

	public void trackListingLastItem(PhotoCollection photoCollection) {
		analytics.trackListingLastItem(photoCollection.getName(), loadedPhotos.size() - 1);
	}

	public static class DownloadPhotoProgress {
		public PhotoDisplayInfo photoDisplayInfo;
		public float progress;

		public DownloadPhotoProgress(PhotoDisplayInfo photoDisplayInfo, Float progress) {
			this.photoDisplayInfo = photoDisplayInfo;
			this.progress = progress;
		}
	}

	public static class DownloadPhotoError {
		public PhotoDisplayInfo photoDisplayInfo;
		public Throwable throwable;

		public DownloadPhotoError(PhotoDisplayInfo photoDisplayInfo, Throwable throwable) {
			this.photoDisplayInfo = photoDisplayInfo;
			this.throwable = throwable;
		}
	}
}
