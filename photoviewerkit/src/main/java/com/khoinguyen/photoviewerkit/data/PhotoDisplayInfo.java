package com.khoinguyen.photoviewerkit.data;

import android.net.Uri;

/**
 * Created by khoinguyen on 4/29/16.
 */
public class PhotoDisplayInfo {
  private String photoId;
  private String highResUrl;
  private String lowResUrl;
  private float ratio;

  public static PhotoDisplayInfo create(String photoId, String highResUrl, String lowResUrl, float ratio) {
    PhotoDisplayInfo photo = new PhotoDisplayInfo();
    photo.setHighResUrl(highResUrl);
    photo.setLowResUrl(lowResUrl);
    photo.setPhotoId(photoId);
    photo.setRatio(ratio);

    return photo;
  }

  public String getHighResUrl() {
    return highResUrl;
  }

  public void setHighResUrl(String highResUrl) {
    this.highResUrl = highResUrl;
  }

  public String getLowResUrl() {
    return lowResUrl;
  }

  public void setLowResUrl(String lowResUrl) {
    this.lowResUrl = lowResUrl;
  }

  public float getRatio() {
    return ratio;
  }

  public void setRatio(float ratio) {
    this.ratio = ratio;
  }

  public Uri getHighResUri() {
    return Uri.parse(getHighResUrl());
  }

  public Uri getLowResUri() {
    return Uri.parse(getLowResUrl());
  }

  public String getPhotoId() {
    return photoId;
  }

  public void setPhotoId(String photoId) {
    this.photoId = photoId;
  }
}
