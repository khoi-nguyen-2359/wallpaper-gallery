package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.PhotoDetailsRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 4/17/16.
 */
public class PhotoDetailsUsecase {
    private PhotoDetailsRepository photoDetailsRepo;

    @Inject
    public PhotoDetailsUsecase(PhotoDetailsRepository photoDetailsRepo) {
        this.photoDetailsRepo = photoDetailsRepo;
    }

    public Observable<Integer> getPageCount(final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int pageCount = photoDetailsRepo.getPageCount(perPage);
                subscriber.onNext(pageCount);
                subscriber.onCompleted();
            }
        });
    }
}
