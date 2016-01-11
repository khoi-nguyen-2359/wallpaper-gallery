package com.xkcn.crawler.presenter;

import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.usecase.PhotoDownloadUsecase;
import com.xkcn.crawler.view.PhotoListingView;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoListingViewPresenter {
    private PhotoListingView view;

    public PhotoListingViewPresenter(PhotoListingView view) {
        this.view = view;
    }

    public void loadWallpaperSetting(PhotoDetails photoDetails) {
        view.showLoading();
        PhotoDownloadUsecase photoDownloadUsecase = PhotoDownloadUsecase.getInstance();
        photoDownloadUsecase.createPhotoDownloadObservable(photoDetails)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PhotoDetails>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoading();
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(PhotoDetails photoDetails) {
                        view.showWallpaperChooser(photoDetails);
                    }
                });
    }
}
