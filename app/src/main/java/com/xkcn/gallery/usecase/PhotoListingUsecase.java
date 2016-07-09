package com.xkcn.gallery.usecase;

import com.xkcn.gallery.data.model.DataPage;
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

  public Observable<DataPage<PhotoDetails>> createHotestPhotoDetailsObservable(final int startIndex, final int perPage) {
    return Observable.create(new Observable.OnSubscribe<DataPage<PhotoDetails>>() {
      @Override
      public void call(Subscriber<? super DataPage<PhotoDetails>> subscriber) {
        List<PhotoDetails> photos = photoDetailsRepository.getHotestPhotos(startIndex, perPage);
        DataPage<PhotoDetails> photoPage = new DataPage<>(photos, startIndex);
        subscriber.onNext(photoPage);
        subscriber.onCompleted();
      }
    });
  }

  public Observable<DataPage<PhotoDetails>> createLatestPhotoDetailsObservable(final int startIndex, final int perPage) {
    return Observable.create(new Observable.OnSubscribe<DataPage<PhotoDetails>>() {
      @Override
      public void call(Subscriber<? super DataPage<PhotoDetails>> subscriber) {
        List<PhotoDetails> photos = photoDetailsRepository.getLatestPhotos(startIndex, perPage);
        DataPage<PhotoDetails> photoPage = new DataPage<>(photos, startIndex);
        subscriber.onNext(photoPage);
        subscriber.onCompleted();
      }
    });
  }
}
