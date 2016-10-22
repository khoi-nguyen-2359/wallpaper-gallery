package com.xkcn.gallery.usecase;

import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.impl.LocalConfigManagerImpl;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 4/17/16.
 */
public class PreferencesUsecase {
	private LocalConfigManager prefRepo;

	public PreferencesUsecase(LocalConfigManager prefRepo) {
		this.prefRepo = prefRepo;
	}

	public Observable<Integer> getListingPagerPerPage() {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				int perPage = LocalConfigManagerImpl.LISTING_PHOTO_PER_PAGE;
				subscriber.onNext(perPage);
				subscriber.onCompleted();
			}
		});
	}

	public Observable<Integer> loadLastWatchedPhotoListPage() {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(prefRepo.getLastWatchedPhotoListPage());
				subscriber.onCompleted();
			}
		});
	}

	public Observable<Object> setLastWatchedPhotoListingPage(final int page) {
		return Observable.create(new Observable.OnSubscribe<Object>() {
			@Override
			public void call(Subscriber<? super Object> subscriber) {
				prefRepo.setLastWatchedPhotoListPage(page);
			}
		});
	}
}
