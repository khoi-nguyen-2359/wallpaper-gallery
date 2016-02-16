package com.xkcn.crawler.presenter;

import com.xkcn.crawler.adapter.PhotoListPagerAdapter;
import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.usecase.PhotoListingUsecase;
import com.xkcn.crawler.view.PhotoListPageView;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListPageViewPresenter {
    private PhotoListPageView view;
    private PhotoListingUsecase photoListingUsecase;
    private int listingType;
    private int photoPage;

    public PhotoListPageViewPresenter(PhotoListingUsecase photoListingUsecase, int listingType, int photoPage) {
        this.photoListingUsecase = photoListingUsecase;
        this.listingType = listingType;
        this.photoPage = photoPage;
    }

    public void setView(PhotoListPageView view) {
        this.view = view;
    }

    public void loadPhotoListPage() {
        Observable<List<PhotoDetails>> photoQueryObservable = null;
        switch (listingType) {
            case PhotoListPagerAdapter.TYPE_HOTEST: {
                photoQueryObservable = photoListingUsecase.createHotestPhotoDetailsObservable(photoPage);
                break;
            }
            case PhotoListPagerAdapter.TYPE_LATEST: {
                photoQueryObservable = photoListingUsecase.createLatestPhotoDetailsObservable(photoPage);
                break;
            }
            default:
                photoQueryObservable = Observable.empty();
        }

        photoQueryObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PhotoDetails>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<PhotoDetails> photos) {
                        view.setupPagerAdapter(photos);
                    }
        });
    }
}
