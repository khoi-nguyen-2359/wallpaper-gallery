package com.xkcn.gallery.presenter;

import com.facebook.common.executors.HandlerExecutorServiceImpl;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoDownloader;
import com.xkcn.gallery.util.LooperPreparedHandler;
import com.xkcn.gallery.view.MainView;

import java.io.File;

import javax.inject.Inject;

import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class MainViewPresenter {
  private MainView view;

  @Inject
  PhotoDownloader photoDownloader;

  @Inject
  PreferenceRepository prefDataStore;

  @Inject
  Scheduler rxIoScheduler;

  public MainViewPresenter(MainView view) {
    this.view = view;
  }

  public void loadWallpaperSetting(PhotoDetails photoDetails) {
    view.showLoading();
    photoDownloader.getPhotoDownloadObservable(photoDetails)
        .subscribeOn(rxIoScheduler)
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

  public void checkToCrawlPhoto() {
    if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - prefDataStore.getUpdatePeriod()) {
      view.startActionUpdate();
    }
  }
}
