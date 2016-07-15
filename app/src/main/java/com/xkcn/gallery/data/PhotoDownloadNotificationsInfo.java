package com.xkcn.gallery.data;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 7/10/16.
 */

public class PhotoDownloadNotificationsInfo {
  private Map<String, Integer> mapDownloadUrlNotificationIds = new HashMap<>();
  private int currentMaxId = 0;

  public int getId(PhotoDetails photoDetails) {
    String downloadUrl = photoDetails.getDefaultDownloadUrl();
    Integer nextId = mapDownloadUrlNotificationIds.get(downloadUrl);
    if (nextId == null) {
      nextId = generateUniqueId();
      mapDownloadUrlNotificationIds.put(downloadUrl, nextId);
    }

    return nextId;
  }

  private Integer generateUniqueId() {
    return ++currentMaxId;
  }
}
