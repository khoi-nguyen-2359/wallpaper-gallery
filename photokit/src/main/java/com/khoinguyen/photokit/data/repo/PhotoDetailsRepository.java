package com.khoinguyen.photokit.data.repo;

import com.khoinguyen.photokit.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface PhotoDetailsRepository {
    long getLargestPhotoId();
    List<PhotoDetails> getLatestPhotos(int page, int perPage);
    List<PhotoDetails> getHotestPhotos(int page, int perPage);
    int addPhotos(List<PhotoDetails> photos);

    PhotoDetails getPhotoDetails(long photoId);

    int updatePhotosStatus(int status);

    int getPageCount(int perPage);
}
