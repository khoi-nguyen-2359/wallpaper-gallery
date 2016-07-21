package com.xkcn.gallery.analytics;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.xkcn.gallery.BuildConfig;
import com.xkcn.gallery.R;

/**
 * Created by khoinguyen on 7/21/16.
 */

public class GoogleAnalytics implements IAnalytics {
  private static final int DISPATCH_PERIOD = BuildConfig.GA_DISPATCH_PERIOD;
  private static final String TRACKER_ID = "UA-81048549-1";
  private Tracker tracker;

  public GoogleAnalytics(Context context) {
    com.google.android.gms.analytics.GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context);
    analytics.setLocalDispatchPeriod(DISPATCH_PERIOD);

    tracker = analytics.newTracker(TRACKER_ID);
    tracker.enableAutoActivityTracking(false);
    tracker.enableExceptionReporting(true);
    tracker.setAnonymizeIp(false);
  }

  @Override
  public void trackListingLength(String categoryName, int lastPhotoIndex) {
    tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Listing")
        .setAction("Load More")
        .set("Category Name", categoryName)
        .set("Last Photo Index", String.valueOf(lastPhotoIndex))
        .build()
    );
  }

  private void trackScreenView(String screenName) {
    tracker.setScreenName(screenName);
    tracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override
  public void trackListingScreenView() {
    trackScreenView("Listing Screen");
  }
}
