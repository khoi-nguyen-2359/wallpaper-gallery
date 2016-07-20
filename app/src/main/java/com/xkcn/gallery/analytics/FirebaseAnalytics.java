package com.xkcn.gallery.analytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by khoinguyen on 7/20/16.
 */

public class FirebaseAnalytics implements IAnalytics {

  private static final String EVENT_LISTING_LOAD_MORE = "Listing Load More";

  private com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics;

  public FirebaseAnalytics(Context context) {
    this.firebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context);
  }

  @Override
  public void trackListingLoadMore(String categoryName, int pageIndex) {
    Bundle params = new Bundle();
    params.putString("Category Name", categoryName);
    params.putInt("Page Index", pageIndex);
    firebaseAnalytics.logEvent(EVENT_LISTING_LOAD_MORE, params);
  }
}
