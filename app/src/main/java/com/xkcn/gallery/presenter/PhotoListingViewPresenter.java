package com.xkcn.gallery.presenter;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.model.DataPage;
import com.xkcn.gallery.data.model.PhotoDetailsDataPage;
import com.xkcn.gallery.di.component.ApplicationComponent;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.view.interfaces.MainView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingViewPresenter {
  public static final int TYPE_HOTEST = 1;
  public static final int TYPE_LATEST = 2;

  private MainView view;

  @Inject
  PhotoListingUsecase photoListingUsecase;

  @Inject
  PreferencesUsecase preferencesUsecase;

  @Inject
  PhotoFileManager photoFileManager;

  @Inject
  Scheduler rxIoScheduler;

  private Observable<Integer> photoPerPageObservable;

  private int currentListingType;

  private PhotoDetailsDataPage photoDetailsPages = new PhotoDetailsDataPage();

  public PhotoListingViewPresenter(ApplicationComponent component) {
    component.inject(this);
    component.inject(photoDetailsPages);
  }

  public void setView(MainView view) {
    this.view = view;
  }

  private Observable<Integer> getPhotoPerPageObservable() {
    return (photoPerPageObservable == null ? photoPerPageObservable = preferencesUsecase.getListingPagerPerPage() : photoPerPageObservable).cache();
  }

  /**
   * @param startIndex  start item to load
   * @param listingType type of current listing
   */
  public void loadPhotoPage(final int startIndex, final int listingType) {
    currentListingType = listingType;
    getPhotoPerPageObservable().flatMap(new Func1<Integer, Observable<DataPage<PhotoDetails>>>() {
      @Override
      public Observable<DataPage<PhotoDetails>> call(Integer perPage) {
        Observable<DataPage<PhotoDetails>> photoQueryObservable = null;
        switch (listingType) {
          case TYPE_HOTEST: {
            photoQueryObservable = photoListingUsecase.createHotestPhotoDetailsObservable(startIndex, perPage);
            break;
          }
          case TYPE_LATEST: {
            photoQueryObservable = photoListingUsecase.createLatestPhotoDetailsObservable(startIndex, perPage);
            break;
          }
          default:
            photoQueryObservable = Observable.empty();
        }

        return photoQueryObservable;
      }
    })
        .doOnNext(new Action1<DataPage<PhotoDetails>>() {
          @Override
          public void call(DataPage<PhotoDetails> photoDetailsDataPage) {
            photoDetailsPages.append(photoDetailsDataPage);
          }
        })    // call append in doOnNext because it takes much time to finish
        .subscribeOn(rxIoScheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<DataPage<PhotoDetails>>() {
          @Override
          public void onCompleted() {
            view.onPagingLoaded();
            if (!photoDetailsPages.hasEnded()) {
              view.enablePaging();
            }
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(DataPage<PhotoDetails> photoPage) {
            L.get().d("next page %d %d", photoPage.getStart(), photoPage.getData().size());
          }
        });
  }

  public List<PhotoDisplayInfo> getAllPhotoDisplayInfos() {
    return photoDetailsPages.getAllDisplayInfos();
  }

  public void loadNextPhotoPage() {
    loadPhotoPage(photoDetailsPages.getNextStart(), currentListingType);
  }

  public PhotoDetails findPhoto(String photoId) {
    List<PhotoDetails> allPhotos = photoDetailsPages.getData();
    for (PhotoDetails photo : allPhotos) {
      if (photo.getIdentifierAsString().equals(photoId)) {
        return photo;
      }
    }

    return null;
  }
}
