package com.khoinguyen.apptemplate.listing.util;

import android.view.View;

/**
 * Created by khoinguyen on 5/17/16.
 */
public class EmptyRecyclerListingViewHolder extends RecyclerListingViewHolder {
  public EmptyRecyclerListingViewHolder(View view) {
    super(view);
  }

  @Override
  public void bind(Object o) {
    // bind nothing
  }
}
