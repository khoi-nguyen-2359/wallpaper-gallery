package com.xkcn.gallery.analytics;

/**
 * Created by khoinguyen on 7/20/16.
 */

public interface IAnalytics {
  /**
   * Track how long did user scroll to view photos.
   * @param categoryName name of showing category to track
   * @param lastPhotoIndex the photo index at end of listing
   */
  void trackListingLength(String categoryName, int lastPhotoIndex);

  void trackListingScreenView();
}
