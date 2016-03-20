package com.xkcn.gallery.data;

import com.xkcn.gallery.data.model.PhotoDetails;

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

    int getPageCount(int perPage);
}
