package com.xkcn.gallery.analytics;

import com.crashlytics.android.Crashlytics;
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
  public void trackListingLastItem(String categoryName, int lastPhotoIndex) {
    for (IAnalytics t : trackers) {
      try {
        t.trackListingLastItem(categoryName, lastPhotoIndex);
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }

  @Override
  public void trackGalleryPhotoScreenView(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      try {
        t.trackGalleryPhotoScreenView(photoDetails);
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }

  @Override
  public void trackListingScreenView() {
    for (IAnalytics t :
        trackers) {
      try {
        t.trackListingScreenView();
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }

  @Override
  public void trackShareGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      try {
        t.trackShareGalleryPhoto(photoDetails);
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }

  @Override
  public void trackSetWallpaperGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      try {
        t.trackSetWallpaperGalleryPhoto(photoDetails);
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }

  @Override
  public void trackDownloadGalleryPhoto(PhotoDetails photoDetails) {
    for (IAnalytics t :
        trackers) {
      try {
        t.trackDownloadGalleryPhoto(photoDetails);
      } catch (Exception ex) {
        Crashlytics.logException(ex);
      }
    }
  }
}
