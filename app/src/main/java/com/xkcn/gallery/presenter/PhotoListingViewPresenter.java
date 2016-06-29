package com.xkcn.gallery.presenter;

import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.model.DataPage;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.usecase.PreferencesUsecase;
import com.xkcn.gallery.view.MainView;

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

  private int currentListingType;

  private DataPage<PhotoDetails> allPages = new DataPage<>();

  public PhotoListingViewPresenter(PhotoListingUsecase photoListingUsecase, PreferencesUsecase preferencesUsecase) {
    this.photoListingUsecase = photoListingUsecase;
    this.preferencesUsecase = preferencesUsecase;
  }

  public void setView(MainView view) {
    this.view = view;
  }

  private Observable<Integer> getPhotoPerPageObservable() {
    return (photoPerPageObservable == null ? photoPerPageObservable = preferencesUsecase.getListingPagerPerPage() : photoPerPageObservable).cache();
  }

  /**
   *
   * @param startIndex start item to load
   * @param listingType type of current listing
   */
  public void loadPhotoPage(final int startIndex, final int listingType) {
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
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<DataPage<PhotoDetails>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(DataPage<PhotoDetails> photoPage) {
            currentListingType = listingType;
            if (photoPage.getStartIndex() == 0) {
              allPages.clear();
            }
            allPages.append(photoPage);
            view.onPagingLoaded();
            if (photoPage.getData() != null && photoPage.getData().size() != 0) {
              view.enablePaging();
            }
          }
        });
  }

  public List<PhotoDetails> getAllPhotos() {
    return allPages.getData();
  }

  public void loadNextPhotoPage() {
    loadPhotoPage(allPages.getNextStartIndex(), currentListingType);
  }

  public PhotoDetails findPhoto(String photoId) {
    List<PhotoDetails> allPhotos = getAllPhotos();
    for (PhotoDetails photo : allPhotos) {
      if (photo.getIdentifierAsString().equals(photoId)) {
        return photo;
      }
    }

    return null;
  }
}
