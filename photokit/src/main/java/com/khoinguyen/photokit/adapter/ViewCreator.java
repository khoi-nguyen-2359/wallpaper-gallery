package com.khoinguyen.photokit.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 5/7/16.
 */
public interface ViewCreator {
  View createView(ViewGroup container);

  ListingViewHolder createViewHolder(View view);
}
