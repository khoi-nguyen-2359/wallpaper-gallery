package com.xkcn.gallery.presenter;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.analytics.AnalyticsCollection;
import com.xkcn.gallery.data.cloud.model.PhotoCollection;
import com.xkcn.gallery.data.local.model.PhotoDetails;
import com.xkcn.gallery.model.DataPage;
import com.xkcn.gallery.model.PhotoDetailsDataPage;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.view.interfaces.PhotoCollectionView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoCollectionViewPresenter {
	@Inject
	PhotoListingUsecase photoListingUsecase;
	@Inject
	PreferencesUsecase preferencesUsecase;
	@Inject
	Scheduler rxIoScheduler;
	@Inject
	AnalyticsCollection analyticsCollection;

	private PhotoCollectionView view;
	private Observable<Integer> photoPerPageObservable;

	private PhotoDetailsDataPage allPages = new PhotoDetailsDataPage();
	private PhotoCollection photoCollection;

	public PhotoCollectionViewPresenter(PhotoCollection photoCollection) {
		this.photoCollection = photoCollection;
	}

	public void setView(PhotoCollectionView view) {
		this.view = view;
	}

	private Observable<Integer> getPhotoPerPageObservable() {
		return (photoPerPageObservable == null ? photoPerPageObservable = preferencesUsecase.getListingPagerPerPage() : photoPerPageObservable).cache();
	}

	public void loadPhotoPage(final int startIndex, final String command) {
		getPhotoPerPageObservable().flatMap(new Func1<Integer, Observable<DataPage<PhotoDetails>>>() {
			@Override
			public Observable<DataPage<PhotoDetails>> call(Integer perPage) {
				return photoListingUsecase.getPhotoPageByCommand(command, startIndex, perPage);
			}
		})
			.doOnNext(new Action1<DataPage<PhotoDetails>>() {
				@Override
				public void call(DataPage<PhotoDetails> nextPageData) {
					checkToTrackingListingLastItem(nextPageData);
				}
			})    // call append in doOnNext because it takes much time to finish
			.map(new Func1<DataPage<PhotoDetails>, DataPage<PhotoDetails>>() {
				@Override
				public DataPage<PhotoDetails> call(DataPage<PhotoDetails> photoDetailsDataPage) {
					allPages.append(photoDetailsDataPage);;
					return allPages;
				}
			})
			.subscribeOn(rxIoScheduler)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<DataPage<PhotoDetails>>() {
				@Override
				public void onCompleted() {
					view.onPagingLoaded();
					if (!allPages.hasEnded()) {
						view.enablePaging();
					}
				}

				@Override
				public void onError(Throwable e) {

				}

				@Override
				public void onNext(DataPage<PhotoDetails> photoPage) {
					L.get().d("next page %d %d", photoPage.getStart(), photoPage.getData().size());
				}
			});
	}

	private void checkToTrackingListingLastItem(DataPage<PhotoDetails> nextPageData) {
		if (nextPageData.getStart() == 0 && allPages.getNextStart() != 0) {  // avoid first load
			trackListingLastItem();
		}
	}

	public void trackListingLastItem() {
		analyticsCollection.trackListingLastItem(photoCollection.getName(), allPages.getNextStart() - 1);
	}

	public PhotoDetailsDataPage getAllPages() {
		return allPages;
	}

	public void loadNextPhotoPage() {
		loadPhotoPage(allPages.getNextStart(), photoCollection.getQuery());
	}

	public PhotoDetails findPhoto(PhotoDisplayInfo photoDisplayInfo) {
		if (photoDisplayInfo == null) {
			return null;
		}

		String photoId = photoDisplayInfo.getPhotoId();
		List<PhotoDetails> allPhotos = allPages.getData();
		for (PhotoDetails photo : allPhotos) {
			if (photo.getIdentifierAsString().equals(photoId)) {
				return photo;
			}
		}

		return null;
	}
}
