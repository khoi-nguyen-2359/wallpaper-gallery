package com.xkcn.gallery.presenter;

import com.xkcn.gallery.adapter.PhotoListPagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoSinglePagerView;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoSinglePagerViewPresenter {
    private PhotoSinglePagerView view;
    private PhotoListingUsecase photoListingUsecase;
    private int listingType;
    private int photoPage;

    public PhotoSinglePagerViewPresenter(PhotoListingUsecase photoListingUsecase, int listingType, int page) {
        this.photoListingUsecase = photoListingUsecase;
        this.listingType = listingType;
        this.photoPage = page;
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
                    public void onNext(List<PhotoDetails> photoDetailses) {
                        view.setupPagerAdapter(photoDetailses);
                    }
                });
    }

    public void setView(PhotoSinglePagerView view) {
        this.view = view;
    }
}
