package com.xkcn.gallery.analytics;

import android.content.Context;
import android.os.Bundle;

import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 7/20/16.
 */

public class FirebaseAnalytics implements IAnalytics {

  private static final String EVENT_SHARE_PHOTO = "share_photo";
  private static final String EVENT_SET_WALLPAPER = "set_wallpaper";
  private static final String EVENT_DOWNLOAD = "download";

  private static final String PARAM_PHOTO_TITLE = "photo_title";
  private static final String PARAM_PHOTO_ID = "photo_id";
  private static final String PARAM_SCREEN_NAME = "screen_name";
  private static final String PARAM_PHOTO_CAT_NAME = "category_name";
  private static final String PARAM_PHOTO_INDEX = "photo_index";

  private static final String VAL_SCREEN_NAME_LISTING = "listing_screen";
  private static final String VAL_SCREEN_NAME_GALLERY = "gallery_screen";

  private static Bundle addPhotoParams(Bundle params, PhotoDetails photoDetails) {
    if (params != null && photoDetails != null) {
      params.putLong(PARAM_PHOTO_ID, photoDetails.getIdentifier());
      params.putString(PARAM_PHOTO_TITLE, photoDetails.getTitle());
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
    params.putString(PARAM_PHOTO_CAT_NAME, categoryName);
    params.putInt(PARAM_PHOTO_INDEX, lastPhotoIndex);
    firebaseAnalytics.logEvent("listing_end_scroll", params);
  }

  private void trackScreenView(String screenName) {
    Bundle params = new Bundle();
    params.putString(PARAM_SCREEN_NAME, screenName);
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
    params.putString(PARAM_SCREEN_NAME, VAL_SCREEN_NAME_GALLERY);
    firebaseAnalytics.logEvent(EVENT_SHARE_PHOTO, params);
  }

  @Override
  public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
    Bundle params = addPhotoParams(new Bundle(), photoDetails);
    params.putString(PARAM_SCREEN_NAME, VAL_SCREEN_NAME_GALLERY);
    firebaseAnalytics.logEvent(EVENT_SET_WALLPAPER, params);
  }

  @Override
  public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
    Bundle params = addPhotoParams(new Bundle(), photoDetails);
    params.putString(PARAM_SCREEN_NAME, VAL_SCREEN_NAME_GALLERY);
    firebaseAnalytics.logEvent(EVENT_DOWNLOAD, params);
  }
}
