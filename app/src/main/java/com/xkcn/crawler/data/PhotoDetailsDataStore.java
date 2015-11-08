package com.xkcn.crawler.data;

import com.xkcn.crawler.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface PhotoDetailsDataStore {
    long getLargestPhotoId();
    List<PhotoDetails> getLatestPhotos();
    List<PhotoDetails> getHotestPhotos();
    int addPhotos(List<PhotoDetails> photos);
}
