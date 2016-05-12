package com.khoinguyen.apptemplate.listing.adapter;

import com.khoinguyen.apptemplate.listing.ItemPart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 5/7/16.
 */
public abstract class PartitionedListingViewAdapter extends RecycledListingViewAdapter {
  protected final List<ItemPart> dataSet = new ArrayList<>();

  public abstract List<ItemPart> updateDataSet();

  @Override
  public int getCount() {
    return dataSet.size();
  }

  @Override
  public Object getViewType(int itemIndex) {
    ItemPart part = dataSet.get(itemIndex);
    return part.getViewType();
  }

  @Override
  public Object getData(int itemIndex) {
    return dataSet.get(itemIndex).getData();
  }
}
