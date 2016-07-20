package com.xkcn.gallery.analytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by khoinguyen on 7/20/16.
 */

public class FirebaseAnalytics implements IAnalytics {

  private static final String EVENT_LISTING_LENGTH = "listing_length";

  private com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics;

  public FirebaseAnalytics(Context context) {
    this.firebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context);
  }

  @Override
  public void trackListingLength(String categoryName, int lastPhotoIndex) {
    Bundle params = new Bundle();
    params.putString("category_name", categoryName);
    params.putInt("last_photo_index", lastPhotoIndex);
    firebaseAnalytics.logEvent(EVENT_LISTING_LENGTH, params);
  }
}
