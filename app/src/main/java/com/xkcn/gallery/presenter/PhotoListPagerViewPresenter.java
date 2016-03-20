package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.view.PhotoListPagerView;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoListPagerViewPresenter {
    private PhotoListPagerView view;
    private PhotoDetailsRepository photoDetailsRepository;
    private PreferenceRepository prefDataStore;

    public PhotoListPagerViewPresenter(PhotoListPagerView view, PhotoDetailsRepository photoDetailsRepository, PreferenceRepository prefDataStore) {
        this.view = view;
        this.photoDetailsRepository = photoDetailsRepository;
        this.prefDataStore = prefDataStore;
    }

    public void loadPageCount() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int pageCount = photoDetailsRepository.getPageCount(prefDataStore.getListPagerPhotoPerPage());
                subscriber.onNext(pageCount);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer pageCount) {
                        view.setupPagerAdapter(pageCount, view.getCurrentType());
                    }
                });
    }

    public void checkToCrawlPhoto() {
        if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
            view.startActionUpdate();
        } else {
            EventBus.getDefault().post(new PhotoCrawlingFinishedEvent());
        }
    }
}
