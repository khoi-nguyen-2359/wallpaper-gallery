package com.khoinguyen.photokit.presenter;

import com.khoinguyen.photokit.adapter.PhotoListingPagerAdapter;
import com.khoinguyen.photokit.usecase.PhotoListingUsecase;
import com.khoinguyen.photokit.usecase.PreferencesUsecase;
import com.khoinguyen.photokit.view.PhotoListingViewPager;
import com.khoinguyen.util.log.L;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoListingViewPagerPresenter {
    protected static final int PHOTO_TYPE_DEFAULT = PhotoListingPagerAdapter.TYPE_LATEST;

    private PhotoListingViewPager view;
    private boolean hasSetLastWatchedPhotoListPage;
    private int currentType = PHOTO_TYPE_DEFAULT;

    private PreferencesUsecase prefUsecase;
    private PhotoListingUsecase photoListingUsecase;

    private L log = L.get(this);

    public PhotoListingViewPagerPresenter(PreferencesUsecase prefUsecase, PhotoListingUsecase photoListingUsecase) {
        this.prefUsecase = prefUsecase;
        this.photoListingUsecase = photoListingUsecase;
    }

    public void loadPageCount() {
        Observable<Integer> getListingPhotoPerPage = prefUsecase.getListingPagerPerPage().cache();
        Observable.zip(getListingPhotoPerPage
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(final Integer perPage) {
                        log.d("getPageCount call in thread %s", Thread.currentThread().getName());
                        return photoListingUsecase.getPageCount(perPage);
                    }
                }), getListingPhotoPerPage, new Func2<Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> call(Integer pageCount, Integer listingPerPage) {
                log.d("zip function call in thread %s", Thread.currentThread().getName());
                return Arrays.asList(pageCount, listingPerPage);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Integer> results) {
                        view.populatePhotoData(results.get(0), results.get(1), currentType);
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

        prefUsecase.loadLastWatchedPhotoListPage()
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
                    public void onNext(Integer page) {
                        view.displayPage(page);
                        hasSetLastWatchedPhotoListPage = true;
                    }
                });
    }

    public void saveLastWatchedPhotoListPage() {
        prefUsecase.setLastWatchedPhotoListingPage(view.getCurrentPagePosition())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.empty());
    }

    public void setView(PhotoListingViewPager view) {
        this.view = view;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
        view.changeListingType(currentType);
    }

    public int getCurrentType() {
        return currentType;
    }

    public PhotoListingPagerAdapter createPhotoListingPagerAdapter() {
        return new PhotoListingPagerAdapter(photoListingUsecase, prefUsecase);
    }
}
