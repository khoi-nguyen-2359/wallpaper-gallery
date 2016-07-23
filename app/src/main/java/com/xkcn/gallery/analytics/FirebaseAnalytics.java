package com.xkcn.gallery.analytics;

import android.content.Context;
import android.os.Bundle;

import com.xkcn.gallery.data.model.PhotoDetails;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

/**
 * Created by khoinguyen on 7/20/16.
 */

public class FirebaseAnalytics implements IAnalytics {

  private static final String EVENT_SHARE_GALLERY_PHOTO = "share_gallery_photo";
  private static final String EVENT_SET_GALLERY_WALLPAPER = "set_gallery_wallpaper";
  private static final String EVENT_DOWNLOAD_GALLERY_PHOTO = "download_gallery_photo";

  private static final String VAL_SCREEN_NAME_LISTING = "listing_screen";
  private static final String VAL_SCREEN_NAME_GALLERY = "gallery_screen";

  private static Bundle addPhotoParams(Bundle params, PhotoDetails photoDetails) {
    if (params != null && photoDetails != null) {
      params.putLong(Param.ITEM_ID, photoDetails.getIdentifier());
      params.putString(Param.ITEM_NAME, photoDetails.getPermalinkMetaAsText());
    }

    return params;
  }

  private com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics;

  public FirebaseAnalytics(Context context) {
    this.firebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context);
  }

  @Override
  public void trackListingEndScroll(String categoryName, int lastPhotoIndex) {
    Bundle params = new Bundle();
    params.putString(Param.ITEM_CATEGORY, categoryName);
    params.putInt(Param.ITEM_LOCATION_ID, lastPhotoIndex);
    firebaseAnalytics.logEvent("listing_end_scroll", params);
  }

  private void trackScreenView(String screenName) {
    Bundle params = new Bundle();
    params.putString(Param.ITEM_NAME, screenName);
    firebaseAnalytics.logEvent("screen_view", params);
  }

  @Override
  public void trackListingScreenView() {
    trackScreenView(VAL_SCREEN_NAME_LISTING);
  }

  @Override
  public void trackGalleryScreenView() {
    trackScreenView(VAL_SCREEN_NAME_GALLERY);
  }

  @Override
  public void trackShareGalleryPhoto(PhotoDetails photoDetails) {
    Bundle params = addPhotoParams(new Bundle(), photoDetails);
    firebaseAnalytics.logEvent(EVENT_SHARE_GALLERY_PHOTO, params);
  }

  @Override
  public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
    Bundle params = addPhotoParams(new Bundle(), photoDetails);
    firebaseAnalytics.logEvent(EVENT_SET_GALLERY_WALLPAPER, params);
  }

  @Override
  public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
    Bundle params = addPhotoParams(new Bundle(), photoDetails);
    firebaseAnalytics.logEvent(EVENT_DOWNLOAD_GALLERY_PHOTO, params);
  }
}
