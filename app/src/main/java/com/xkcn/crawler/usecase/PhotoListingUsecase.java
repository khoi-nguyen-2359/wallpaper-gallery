package com.xkcn.crawler.usecase;

import com.xkcn.crawler.data.PhotoDetailsRepository;
import com.xkcn.crawler.data.model.PhotoDetails;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase {
    private PhotoDetailsRepository photoDetailsRepository;
    private int perPage;

    public PhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository, int perPage) {
        this.photoDetailsRepository = photoDetailsRepository;
        this.perPage = perPage;
    }

    public Observable<List<PhotoDetails>> createHotestPhotoDetailsObservable(final int page) {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDetails>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDetails>> subscriber) {
                List<PhotoDetails> hotestPhotos = photoDetailsRepository.getHotestPhotos(page, perPage);
                subscriber.onNext(hotestPhotos);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<PhotoDetails>> createLatestPhotoDetailsObservable(final int page) {
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
