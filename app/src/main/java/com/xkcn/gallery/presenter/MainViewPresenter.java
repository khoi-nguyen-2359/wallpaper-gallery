package com.xkcn.gallery.presenter;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.view.MainView;

import javax.inject.Inject;

import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class MainViewPresenter {
  private MainView view;

  @Inject
  PhotoFileManager photoFileManager;

  @Inject
  PreferenceRepository prefDataStore;

  @Inject
  Scheduler rxIoScheduler;

  public MainViewPresenter(MainView view) {
    this.view = view;
  }

  public void loadWallpaperSetting(final PhotoDetails photoDetails) {
    view.showLoading();
    photoFileManager.getPhotoFileObservable(photoDetails)
        .subscribeOn(rxIoScheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Float>() {
          @Override
          public void onCompleted() {
            L.get().d("set wallpaper onCompleted");

            view.hideLoading();
            view.showWallpaperChooser(photoFileManager.getPhotoFile(photoDetails));
          }

          @Override
          public void onError(Throwable e) {
            L.get().d("set wallpaper onError");

            view.hideLoading();
            view.showToast(e.getMessage());
          }

          @Override
          public void onNext(Float progress) {
            L.get().d("set wallpaper progress = %f", progress);
          }
        });

  }

  public void checkToCrawlPhoto() {
    if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
      view.startActionUpdate();
    }
  }

  public void downloadPhoto(final PhotoDetails photoDetails) {
    if (photoDetails == null) {
      return;
    }

    photoFileManager.getPhotoFileObservable(photoDetails)
        .subscribeOn(rxIoScheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Float>() {
          @Override
          public void onCompleted() {
            L.get().d("download onCompleted");
            view.showDownloadComplete(photoDetails);
          }

          @Override
          public void onError(Throwable e) {
            L.get().d("download onError");
            view.showDownloadError(photoDetails, e.getMessage());
          }

          @Override
          public void onNext(Float progress) {
            L.get().d("download onNext %s", progress);
            view.updateDownloadProgress(photoDetails, progress);
          }
        });
  }
}
