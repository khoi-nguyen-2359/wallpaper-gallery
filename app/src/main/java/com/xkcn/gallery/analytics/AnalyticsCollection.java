package com.xkcn.gallery.analytics;

import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 7/20/16.
 */

public class AnalyticsCollection implements IAnalytics {
  private List<IAnalytics> trackers = new ArrayList<>();

  public void addTracker(IAnalytics tracker) {
    if (tracker == null) {
      return;
    }

    trackers.add(tracker);
  }

  @Override
  public void trackListingEndScroll(String categoryName, int lastPhotoIndex) {
    for (IAnalytics t : trackers) {
      t.trackListingEndScroll(categoryName, lastPhotoIndex);
    }
  }

  @Override
  public void trackGalleryPhotoScreenView(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      t.trackGalleryPhotoScreenView(photoDetails);
    }
  }

  @Override
  public void trackListingScreenView() {
    for (IAnalytics t :
        trackers) {
      t.trackListingScreenView();
    }
  }

  @Override
  public void trackShareGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      t.trackShareGalleryPhoto(photoDetails);
    }
  }

  @Override
  public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      t.trackSetWallpaperGalleryPhoto(photoDetails);
    }
  }

  @Override
  public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      t.trackDownloadGalleryPhoto(photoDetails);
    }
  }
}
