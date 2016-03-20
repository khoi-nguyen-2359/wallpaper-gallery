package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.imageloader.PhotoDownloader;
import com.xkcn.gallery.view.PhotoListingView;

import java.io.File;

import rx.Observer;
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
        photoDownloader.getPhotoDownloadObservable(photoDetails)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
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
                    public void onNext(File photoUrl) {
                        view.showWallpaperChooser(photoUrl);

                    }
                });
    }

    public void setView(PhotoListingView view) {
        this.view = view;
    }
}
