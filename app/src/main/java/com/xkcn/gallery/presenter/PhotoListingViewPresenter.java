package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.model.PhotoDetailsPage;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.view.MainView;

import java.util.ArrayList;
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
  public static final int TYPE_HOTEST = 1;
  public static final int TYPE_LATEST = 2;

  private MainView view;

  private PhotoListingUsecase photoListingUsecase;
  private PreferencesUsecase preferencesUsecase;

  private Observable<Integer> photoPerPageObservable;

  private List<PhotoDetails> allPhotos = new ArrayList<>();
  private int currentListingType;

  public PhotoListingViewPresenter(PhotoListingUsecase photoListingUsecase, PreferencesUsecase preferencesUsecase) {
    this.photoListingUsecase = photoListingUsecase;
    this.preferencesUsecase = preferencesUsecase;
  }

  public void setView(MainView view) {
    this.view = view;
  }

  public void clearPhotos() {
    allPhotos.clear();
  }

  private Observable<Integer> getPhotoPerPageObservable() {
    return (photoPerPageObservable == null ? photoPerPageObservable = preferencesUsecase.getListingPagerPerPage() : photoPerPageObservable).cache();
  }

  /**
   *
   * @param aPageItem provide position of an arbitrary item in the page to load. This will help to infer the page to load.
   * @param listingType type of current listing
   */
  public void loadPhotoPage(final int aPageItem, final int listingType) {
    getPhotoPerPageObservable().flatMap(new Func1<Integer, Observable<PhotoDetailsPage>>() {
      @Override
      public Observable<PhotoDetailsPage> call(Integer perPage) {
        int page = aPageItem / perPage + 1;
        Observable<PhotoDetailsPage> photoQueryObservable = null;
        switch (listingType) {
          case TYPE_HOTEST: {
            photoQueryObservable = photoListingUsecase.createHotestPhotoDetailsObservable(page, perPage);
            break;
          }
          case TYPE_LATEST: {
            photoQueryObservable = photoListingUsecase.createLatestPhotoDetailsObservable(page, perPage);
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
        .subscribe(new Observer<PhotoDetailsPage>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(PhotoDetailsPage photoDetailsPage) {
            currentListingType = listingType;
            if (photoDetailsPage.getPage() == 1) {
              allPhotos.clear();
            }
            allPhotos.addAll(photoDetailsPage.getPhotos());
            view.appendPhotoData(photoDetailsPage);
          }
        });
  }

  public List<PhotoDetails> getAllPhotos() {
    return allPhotos;
  }

  public void loadNextPhotoPage() {
    loadPhotoPage(allPhotos.size(), currentListingType);
  }
}
