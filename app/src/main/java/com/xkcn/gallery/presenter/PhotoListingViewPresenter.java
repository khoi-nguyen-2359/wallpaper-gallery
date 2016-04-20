package com.xkcn.gallery.presenter;

import com.xkcn.gallery.adapter.PhotoListingPagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoListingView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingViewPresenter {
    private PhotoListingView view;
    private int perPage;

    @Inject PhotoListingUsecase photoListingUsecase;

    public PhotoListingViewPresenter(int perPage) {
        this.perPage = perPage;
    }

    public void setView(PhotoListingView view) {
        this.view = view;
    }

    public void loadPhotoListPage(int photoPage, int listingType) {
        Observable<List<PhotoDetails>> photoQueryObservable = null;
        switch (listingType) {
            case PhotoListingPagerAdapter.TYPE_HOTEST: {
                photoQueryObservable = photoListingUsecase.createHotestPhotoDetailsObservable(photoPage, perPage);
                break;
            }
            case PhotoListingPagerAdapter.TYPE_LATEST: {
                photoQueryObservable = photoListingUsecase.createLatestPhotoDetailsObservable(photoPage, perPage);
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
                        view.displayPhotoData(photos);
                    }
        });
    }
}
