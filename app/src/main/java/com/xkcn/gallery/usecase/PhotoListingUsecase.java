package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.model.PhotoDetailsPage;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.repo.PhotoDetailsRepository;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/18/15.
 */
public class PhotoListingUsecase {
  private PhotoDetailsRepository photoDetailsRepository;

  public PhotoListingUsecase(PhotoDetailsRepository photoDetailsRepository) {
    this.photoDetailsRepository = photoDetailsRepository;
  }

  public Observable<Integer> getPageCount(final int perPage) {
    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override
      public void call(Subscriber<? super Integer> subscriber) {
        int pageCount = photoDetailsRepository.getPageCount(perPage);
        subscriber.onNext(pageCount);
        subscriber.onCompleted();
      }
    });
  }

  public Observable<PhotoDetailsPage> createHotestPhotoDetailsObservable(final int page, final int perPage) {
    return Observable.create(new Observable.OnSubscribe<PhotoDetailsPage>() {
      @Override
      public void call(Subscriber<? super PhotoDetailsPage> subscriber) {
        List<PhotoDetails> photos = photoDetailsRepository.getHotestPhotos(page, perPage);
        PhotoDetailsPage photoPage = new PhotoDetailsPage();
        photoPage.setPage(page);
        photoPage.setPhotos(photos);
        subscriber.onNext(photoPage);
        subscriber.onCompleted();
      }
    });
  }

  public Observable<PhotoDetailsPage> createLatestPhotoDetailsObservable(final int page, final int perPage) {
    return Observable.create(new Observable.OnSubscribe<PhotoDetailsPage>() {
      @Override
      public void call(Subscriber<? super PhotoDetailsPage> subscriber) {
        List<PhotoDetails> photos = photoDetailsRepository.getLatestPhotos(page, perPage);
        PhotoDetailsPage photoPage = new PhotoDetailsPage();
        photoPage.setPage(page);
        photoPage.setPhotos(photos);
        subscriber.onNext(photoPage);
        subscriber.onCompleted();
      }
    });
  }
}
