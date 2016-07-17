package com.khoinguyen.photoviewerkit.impl.util;

import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
import com.khoinguyen.photoviewerkit.R;

/**
 * Created by khoinguyen on 7/17/16.
 */
public abstract class BottomLoadingIndicatorAdapter extends PartitionedListingAdapter<RecyclerListingViewHolder> {
  private boolean showIndicator = false;

  private static final int TYPE_LOADING_INDICATOR = 1;

  public BottomLoadingIndicatorAdapter() {
    registerListingItemType(new IndicatorType());
  }

  @Override
  public void updateDataSet() {
    super.updateDataSet();

    if (showIndicator) {
      ListingItem indicatorItem = new ListingItem(null, new IndicatorType());
      dataSet.add(indicatorItem);
    }
  }

  public void showIndicator(boolean showIndicator) {
    this.showIndicator = showIndicator;
  }

  public boolean isIndicatorShown() {
    return showIndicator;
  }

  /**
   * @param position
   * @return True if the given value is position of indicator at end of data set. Otherwise False
   */
  public boolean isIndicator(int position) {
    return position + 1 == getCount() && isIndicatorShown();
  }

  private class IndicatorType extends ListingItemType<RecyclerListingViewHolder> {

    public IndicatorType() {
      super(TYPE_LOADING_INDICATOR);
    }

    @Override
    public View createView(ViewGroup container) {
      return getLayoutInflater(container.getContext()).inflate(R.layout.photokit_photo_listing_loading_indicator, container, false);
    }

    @Override
    public RecyclerListingViewHolder createViewHolder(View view) {
      return new RecyclerListingViewHolder(view);
    }
  }
}
