package com.khoinguyen.photokit.usecase;

import com.khoinguyen.photokit.data.model.PhotoDetails;
import com.khoinguyen.photokit.data.repo.PhotoDetailsRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepository;
import com.khoinguyen.photokit.data.repo.PreferenceRepositoryImpl;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase {
    private PhotoDetailsRepository photoDetailsRepository;

    public PhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository) {
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

    public Observable<List<PhotoDetails>> createHotestPhotoDetailsObservable(final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDetails>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDetails>> subscriber) {
                List<PhotoDetails> hotestPhotos = photoDetailsRepository.getHotestPhotos(page, perPage);
                subscriber.onNext(hotestPhotos);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<PhotoDetails>> createLatestPhotoDetailsObservable(final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDetails>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDetails>> subscriber) {
                List<PhotoDetails> hotestPhotos = photoDetailsRepository.getLatestPhotos(page, perPage);
                subscriber.onNext(hotestPhotos);
                subscriber.onCompleted();
            }
        });
    }
}
