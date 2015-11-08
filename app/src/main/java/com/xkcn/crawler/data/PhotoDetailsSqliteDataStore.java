package com.xkcn.crawler.data;

import com.xkcn.crawler.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class PhotoDetailsSqliteDataStore implements PhotoDetailsDataStore {
    @Override
    public long getLargestPhotoId() {
        return 0;
    }

    @Override
    public List<PhotoDetails> getLatestPhotos() {
        return null;
    }

    @Override
    public List<PhotoDetails> getHotestPhotos() {
        return null;
    }

    @Override
    public int addPhotos(List<PhotoDetails> photos) {
        return 0;
    }
}
