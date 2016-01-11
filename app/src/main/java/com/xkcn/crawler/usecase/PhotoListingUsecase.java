package com.xkcn.crawler.usecase;

import com.xkcn.crawler.data.PhotoDetailsDataStore;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.model.PhotoDetails;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase {
    private PhotoDetailsDataStore photoDetailsDataStore;
    private int perPage;

    public PhotoListingUsecase(PhotoDetailsDataStore photoDetailsDataStore, int perPage) {
        this.photoDetailsDataStore = photoDetailsDataStore;
        this.perPage = perPage;
    }

    public Observable<List<PhotoDetails>> createHotestPhotoDetailsObservable(final int page) {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDetails>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDetails>> subscriber) {
                List<PhotoDetails> hotestPhotos = photoDetailsDataStore.getHotestPhotos(page, perPage);
                subscriber.onNext(hotestPhotos);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<PhotoDetails>> createLatestPhotoDetailsObservable(final int page) {
        return Observable.create(new Observable.OnSubscribe<List<PhotoDetails>>() {
            @Override
            public void call(Subscriber<? super List<PhotoDetails>> subscriber) {
                List<PhotoDetails> hotestPhotos = photoDetailsDataStore.getLatestPhotos(page, perPage);
                subscriber.onNext(hotestPhotos);
                subscriber.onCompleted();
            }
        });
    }
}
