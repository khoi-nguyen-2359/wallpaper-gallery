package com.xkcn.crawler.presenter;

import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.usecase.PhotoDownloader;
import com.xkcn.crawler.view.PhotoListingView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoListingViewPresenter {
    private PhotoListingView view;
    private PhotoDownloader photoDownloader;

    public PhotoListingViewPresenter(PhotoDownloader photoDownloader) {
        this.photoDownloader = photoDownloader;
    }

    public void loadWallpaperSetting(PhotoDetails photoDetails) {
        view.showLoading();
        photoDownloader.createPhotoDownloadObservable(photoDetails)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<PhotoDetails>() {
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
//                .subscribe(new Observer<PhotoDetails>() {
//                    @Override
//                    public void onCompleted() {
//                        view.hideLoading();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        view.hideLoading();
//                        view.showToast(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(PhotoDetails photoDetails) {
//                        view.showWallpaperChooser(photoDetails);
//                    }
//                });
    }

    public void setView(PhotoListingView view) {
        this.view = view;
    }

    public void setView(PhotoListingView view) {
        this.view = view;
    }
}
