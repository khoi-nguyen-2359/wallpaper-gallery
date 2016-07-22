package com.xkcn.gallery.analytics;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.xkcn.gallery.BuildConfig;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 7/21/16.
 */

public class GoogleAnalytics implements IAnalytics {
  private static final int DISPATCH_PERIOD = BuildConfig.GA_DISPATCH_PERIOD;
  private static final String TRACKER_ID = "UA-81048549-1";

  private static final String CAT_SHARE = "Share";
  private static final String CAT_SET_WALLPAPER = "Set Wallpaper";
  private static final String CAT_DOWNLOAD = "Download";

  private static final int DIMEN_PHOTO_CAT = 1;
  private static final int DIMEN_PHOTO_ID = 2;
  private static final int DIMEN_PHOTO_TITLE = 4;
  private static final int DIMEN_SCREEN_TITLE = 5;

  private static final int METRIC_LAST_PHOTO_INDEX = 1;

  private static final String VAL_SCREEN_TITLE_GALLERY = "Gallery Screen";
  private static final String VAL_SCREEN_TITLE_LISTING = "Listing Screen";

  private Tracker tracker;

  private static HitBuilders.EventBuilder buildPhotoDimensions(HitBuilders.EventBuilder builder, PhotoDetails photoDetails) {
    if (builder != null && photoDetails != null) {
      builder.setCustomDimension(DIMEN_PHOTO_ID, photoDetails.getIdentifierAsString())
          .setCustomDimension(DIMEN_PHOTO_TITLE, photoDetails.getTitle());
    }
    return builder;
  }

  public GoogleAnalytics(Context context) {
    com.google.android.gms.analytics.GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context);
    analytics.setLocalDispatchPeriod(DISPATCH_PERIOD);

    tracker = analytics.newTracker(TRACKER_ID);
    tracker.enableAutoActivityTracking(false);
    tracker.enableExceptionReporting(true);
    tracker.setAnonymizeIp(false);
  }

  @Override
  public void trackListingEndScroll(String categoryName, int lastPhotoIndex) {
    tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Listing")
        .setAction("Scroll")
        .setLabel("End")
        .setCustomDimension(DIMEN_PHOTO_CAT, categoryName)
        .setCustomMetric(METRIC_LAST_PHOTO_INDEX, lastPhotoIndex)
        .build()
    );
  }

  private void trackScreenView(String screenName) {
    tracker.setScreenName(screenName);
    tracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override
  public void trackListingScreenView() {
    trackScreenView(VAL_SCREEN_TITLE_LISTING);
  }

  @Override
  public void trackGalleryScreenView() {
    trackScreenView(VAL_SCREEN_TITLE_GALLERY);
  }

  @Override
  public void trackShareGalleryPhoto(PhotoDetails photoDetails) {
    tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
        .setCategory(CAT_SHARE)
        .setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY)
        .build()
    );
  }

  @Override
  public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
    tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
        .setCategory(CAT_SET_WALLPAPER)
        .setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY)
        .build()
    );
  }

  @Override
  public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
    tracker.send(buildPhotoDimensions(new HitBuilders.EventBuilder(), photoDetails)
        .setCategory(CAT_DOWNLOAD)
        .setCustomDimension(DIMEN_SCREEN_TITLE, VAL_SCREEN_TITLE_GALLERY)
        .build()
    );
  }
}
