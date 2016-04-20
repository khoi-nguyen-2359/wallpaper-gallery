package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase {
    private PhotoDetailsRepository photoDetailsRepository;

    @Inject
    public PhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository) {
        this.photoDetailsRepository = photoDetailsRepository;
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
