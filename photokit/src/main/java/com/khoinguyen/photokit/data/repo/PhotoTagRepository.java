package com.khoinguyen.photokit.data.repo;

import com.khoinguyen.photokit.data.model.PhotoTag;

import java.util.List;

/**
 * Created by khoinguyen on 2/2/16.
 */
public interface PhotoTagRepository {
    int addTags(List<PhotoTag> tags);

    int updatePhotosStatus(int status);
}
