package com.xkcn.gallery.presenter;

import android.support.v4.view.WindowInsetsCompat;

import com.xkcn.gallery.adapter.PhotoListingPagerAdapter;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.view.PhotoListingViewPager;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoListingViewPagerPresenter {
    protected static final int PHOTO_TYPE_DEFAULT = PhotoListingPagerAdapter.TYPE_LATEST;

    private PhotoListingViewPager view;
    private boolean hasSetLastWatchedPhotoListPage;
    private PreferenceRepository prefDataStore;
    private PhotoDetailsRepository photoDetailsRepository;
    private int currentType = PHOTO_TYPE_DEFAULT;

    public PhotoListingViewPagerPresenter(PreferenceRepository prefDataStore, PhotoDetailsRepository photoDetailsRepository) {
        this.prefDataStore = prefDataStore;
        this.photoDetailsRepository = photoDetailsRepository;
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
                        view.displayPhotoPages(pageCount, currentType);
                    }
                });
    }

    /**
     * Only load and set last watched page for the very first time opening this screen.
     */
    public void loadLastWatchedPhotoListPage() {
        if (hasSetLastWatchedPhotoListPage) {
            return;
        }

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(prefDataStore.getLastWatchedPhotoListPage());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer page) {
                        view.setCurrentPage(page);
                        hasSetLastWatchedPhotoListPage = true;
                    }
                });
    }

    public void saveLastWatchedPhotoListPage() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                prefDataStore.setLastWatchedPhotoListPage(view.getCurrentPage());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.empty());
    }

    public void setView(PhotoListingViewPager view) {
        this.view = view;
    }

    public void onApplyWindowInsets(WindowInsetsCompat insets) {
        view.onApplyWindowInsets(insets);
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
        view.changeListingType(currentType);
    }

    public int getCurrentType() {
        return currentType;
    }
}
