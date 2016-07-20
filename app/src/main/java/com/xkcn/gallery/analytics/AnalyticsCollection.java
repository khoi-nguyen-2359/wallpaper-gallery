package com.xkcn.gallery.analytics;

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
  public void trackListingLoadMore(String categoryName, int pageIndex) {
    for (IAnalytics t : trackers) {
      t.trackListingLoadMore(categoryName, pageIndex);
    }
  }
}
