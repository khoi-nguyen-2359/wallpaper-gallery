package com.khoinguyen.apptemplate.listing;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 5/7/16.
 */
public interface ItemCreator {
  View createView(ViewGroup container);

  ItemViewHolder createViewHolder(View view);
}
