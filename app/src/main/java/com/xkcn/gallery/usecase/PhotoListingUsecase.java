package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.local.model.PhotoDetails;
import com.xkcn.gallery.data.local.repo.PhotoDetailsRepository;
import com.xkcn.gallery.model.DataPage;

import java.util.List;
import java.util.concurrent.Executor;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase extends ScheduledUsecase {
	private PhotoDetailsRepository photoDetailsRepository;

	public PhotoListingUsecase(Executor workerExecutor, Executor callbackExecutor, PhotoDetailsRepository photoDetailsRepository) {
		super(workerExecutor, callbackExecutor);
		this.photoDetailsRepository = photoDetailsRepository;
	}

	public Observable<Integer> getPageCount(final int perPage) {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				int pageCount = photoDetailsRepository.getPageCount(perPage);
				subscriber.onNext(pageCount);
				subscriber.onCompleted();
			}
		});
	}

	public Observable<List<PhotoDetails>> queryPhotos(final String query, final int start, final int count) {
		return Observable.defer(() -> Observable.just(photoDetailsRepository.getPhotoDetails(query, start, count)))
			.compose(createSchedulerTransformer());
	}

	public Observable<DataPage<PhotoDetails>> createHotestPhotoDetailsObservable(final int startIndex, final int count) {
		return Observable.create(new Observable.OnSubscribe<DataPage<PhotoDetails>>() {
			@Override
			public void call(Subscriber<? super DataPage<PhotoDetails>> subscriber) {
				List<PhotoDetails> photos = photoDetailsRepository.getHotestPhotos(startIndex, count);
				DataPage<PhotoDetails> photoPage = new DataPage<>(photos, startIndex);
				subscriber.onNext(photoPage);
				subscriber.onCompleted();
			}
		});
	}

	public Observable<DataPage<PhotoDetails>> createLatestPhotoDetailsObservable(final int startIndex, final int perPage) {
		return Observable.create(new Observable.OnSubscribe<DataPage<PhotoDetails>>() {
			@Override
			public void call(Subscriber<? super DataPage<PhotoDetails>> subscriber) {
				List<PhotoDetails> photos = photoDetailsRepository.getLatestPhotos(startIndex, perPage);
				DataPage<PhotoDetails> photoPage = new DataPage<>(photos, startIndex);
				subscriber.onNext(photoPage);
				subscriber.onCompleted();
			}
		});
	}
}
