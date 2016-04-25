package com.khoinguyen.photokit.presenter;

import com.khoinguyen.photokit.adapter.PhotoListingPagerAdapter;
import com.khoinguyen.photokit.data.model.PhotoDetails;
import com.khoinguyen.photokit.usecase.PhotoListingUsecase;
import com.khoinguyen.photokit.usecase.PreferencesUsecase;
import com.khoinguyen.photokit.view.PhotoListingView;

import java.util.List;


import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingViewPresenter {
    private PhotoListingView view;

    private PhotoListingUsecase photoListingUsecase;
    private PreferencesUsecase preferencesUsecase;

    private List<PhotoDetails> cachedPhotos;

    public PhotoListingViewPresenter(PhotoListingUsecase photoListingUsecase, PreferencesUsecase preferencesUsecase) {
        this.photoListingUsecase = photoListingUsecase;
        this.preferencesUsecase = preferencesUsecase;
    }

    public void setView(PhotoListingView view) {
        this.view = view;
    }

    public void loadPhotoListPage(final int photoPage, final int listingType) {
        preferencesUsecase.getListingPagerPerPage().flatMap(new Func1<Integer, Observable<List<PhotoDetails>>>() {
            @Override
            public Observable<List<PhotoDetails>> call(Integer perPage) {
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

                return photoQueryObservable;
            }
        })
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
                        cachedPhotos = photos;
                        view.populatePhotoData(photos);
                    }
        });
    }

    public PhotoDetails getCachedPhotoDetails(int position) {
        if (cachedPhotos == null || position >= cachedPhotos.size()) {
            return null;
        }

        return cachedPhotos.get(position);
    }
}
