package com.xkcn.gallery.data.local.repo;

import com.xkcn.gallery.data.local.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public interface PhotoDetailsRepository {
	long getLargestPhotoId();

	List<PhotoDetails> getLatestPhotos(int startIndex, int count);

	List<PhotoDetails> getHotestPhotos(int startIndex, int count);

	int addPhotos(List<PhotoDetails> photos);

	PhotoDetails getPhotoDetails(long photoId);

	int updatePhotosStatus(int status);

	int getPageCount(int perPage);

	List<PhotoDetails> getPhotoDetails(String cmd, int start, int count);
}
