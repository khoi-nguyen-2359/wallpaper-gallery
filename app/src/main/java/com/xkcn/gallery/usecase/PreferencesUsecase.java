package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.repo.PreferenceRepository;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 4/17/16.
 */
public class PreferencesUsecase {
	private PreferenceRepository prefRepo;

	public PreferencesUsecase(PreferenceRepository prefRepo) {
		this.prefRepo = prefRepo;
	}

	public Observable<Integer> getListingPagerPerPage() {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				int perPage = prefRepo.getListPagerPhotoPerPage();
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
