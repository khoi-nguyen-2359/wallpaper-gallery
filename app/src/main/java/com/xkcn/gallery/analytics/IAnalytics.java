package com.xkcn.gallery.analytics;

import com.xkcn.gallery.data.model.PhotoDetails;

/**
 * Created by khoinguyen on 7/20/16.
 */

public interface IAnalytics {
  /**
   * Track how long did user scroll to view photos.
   * @param categoryName name of showing category to track
   * @param lastPhotoIndex the photo index at end of listing
   */
  void trackListingLastItem(String categoryName, int lastPhotoIndex);

  void trackGalleryPhotoScreenView(PhotoDetails photoDetails);

  void trackListingScreenView();

  void trackShareGalleryPhoto(PhotoDetails photoDetails);
  void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails);
  void trackDownloadGalleryPhoto(PhotoDetails photoDetails);
}
