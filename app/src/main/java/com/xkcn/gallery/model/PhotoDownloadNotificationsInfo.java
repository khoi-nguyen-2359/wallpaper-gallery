package com.xkcn.gallery.model;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 7/10/16.
 */

public class PhotoDownloadNotificationsInfo {
	private Map<String, Integer> mapPhotoIdToNotificationIds = new HashMap<>();
	private int currentMaxId = 0;

	public int getId(PhotoDisplayInfo photoDisplayInfo) {
		String photoId = photoDisplayInfo.getPhotoId();
		Integer nextId = mapPhotoIdToNotificationIds.get(photoId);
		if (nextId == null) {
			nextId = generateUniqueId();
			mapPhotoIdToNotificationIds.put(photoId, nextId);
		}

		return nextId;
	}

	private Integer generateUniqueId() {
		return ++currentMaxId;
	}
}
