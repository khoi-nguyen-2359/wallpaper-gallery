package com.xkcn.gallery.presenter;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.data.model.PhotoCategory;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.model.DataPage;
import com.xkcn.gallery.data.model.PhotoDetailsDataPage;
import com.xkcn.gallery.di.component.ApplicationComponent;
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
  private MainView view;

  @Inject
  PhotoListingUsecase photoListingUsecase;

  @Inject
  PreferencesUsecase preferencesUsecase;

  @Inject
  Scheduler rxIoScheduler;

  private Observable<Integer> photoPerPageObservable;

  private PhotoCategory currentListingType;

  private PhotoDetailsDataPage allPages = new PhotoDetailsDataPage();

  public PhotoListingViewPresenter(ApplicationComponent component) {
    component.inject(this);
    component.inject(allPages);
  }

  public void setView(MainView view) {
    this.view = view;
  }

  private Observable<Integer> getPhotoPerPageObservable() {
    return (photoPerPageObservable == null ? photoPerPageObservable = preferencesUsecase.getListingPagerPerPage() : photoPerPageObservable).cache();
  }

  /**
   * @param startIndex  start item to load
   * @param category type of current listing
   */
  public void loadPhotoPage(final int startIndex, final PhotoCategory category) {
    currentListingType = category;
    getPhotoPerPageObservable().flatMap(new Func1<Integer, Observable<DataPage<PhotoDetails>>>() {
      @Override
      public Observable<DataPage<PhotoDetails>> call(Integer perPage) {
        Observable<DataPage<PhotoDetails>> photoQueryObservable = null;
        if (category.getId() == PhotoCategory.HOSTEST.getId()) {
          photoQueryObservable = photoListingUsecase.createHotestPhotoDetailsObservable(startIndex, perPage);
        } else if (category.getId() == PhotoCategory.LATEST.getId()) {
          photoQueryObservable = photoListingUsecase.createLatestPhotoDetailsObservable(startIndex, perPage);
        } else {
          photoQueryObservable = Observable.empty();
        }

        return photoQueryObservable;
      }
    })
        .doOnNext(new Action1<DataPage<PhotoDetails>>() {
          @Override
          public void call(DataPage<PhotoDetails> photoDetailsDataPage) {
            allPages.append(photoDetailsDataPage);
          }
        })    // call append in doOnNext because it takes much time to finish
        .subscribeOn(rxIoScheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<DataPage<PhotoDetails>>() {
          @Override
          public void onCompleted() {
            view.onPagingLoaded();
            if (!allPages.hasEnded()) {
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

  public PhotoDetailsDataPage getAllPages() {
    return allPages;
  }

  public void loadNextPhotoPage() {
    loadPhotoPage(allPages.getNextStart(), currentListingType);
  }

  public PhotoDetails findPhoto(PhotoDisplayInfo photoDisplayInfo) {
    if (photoDisplayInfo == null) {
      return null;
    }

    String photoId = photoDisplayInfo.getPhotoId();
    List<PhotoDetails> allPhotos = allPages.getData();
    for (PhotoDetails photo : allPhotos) {
      if (photo.getIdentifierAsString().equals(photoId)) {
        return photo;
      }
    }

    return null;
  }

  public PhotoCategory getCurrentListingType() {
    return currentListingType;
  }
}
