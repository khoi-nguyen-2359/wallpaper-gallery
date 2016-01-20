package com.xkcn.crawler.presenter;

import com.xkcn.crawler.adapter.PhotoListPagerAdapter;
import com.xkcn.crawler.data.PhotoDetailsDataStore;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.view.PhotoListPagerView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoListPagerViewPresenter {
    private PhotoListPagerView view;
    private PhotoDetailsDataStore photoDetailsDataStore;
    private PreferenceDataStore prefDataStore;

    public PhotoListPagerViewPresenter(PhotoListPagerView view, PhotoDetailsDataStore photoDetailsDataStore, PreferenceDataStore prefDataStore) {
        this.view = view;
        this.photoDetailsDataStore = photoDetailsDataStore;
        this.prefDataStore = prefDataStore;
    }

    public void loadPageCount() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int pageCount = photoDetailsDataStore.getPageCount(prefDataStore.getListPagerPhotoPerPage());
                subscriber.onNext(pageCount);
                subscriber.onCompleted();
            }
        }).observeOn(Schedulers.newThread())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Integer>() {
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
        });;
    }
}
