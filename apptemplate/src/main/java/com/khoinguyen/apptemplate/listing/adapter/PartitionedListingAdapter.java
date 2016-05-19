package com.khoinguyen.apptemplate.listing.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.util.log.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 5/9/16.
 * <p/>
 * This adapter provides item views/viewholders based on item view type.
 */
public abstract class PartitionedListingAdapter<VH extends IViewHolder> implements ListingAdapter<VH> {
  protected SparseArrayCompat<ListingItemType<VH>> itemTypeRegistry = new SparseArrayCompat<>();
  protected DataObservable dataObservable = new DataObservable();

  private L log = L.get("PartitionedListingAdapter");

  protected final List<ListingItem> dataSet = new ArrayList<>();

  protected abstract List<ListingItem> createDataSet();

  @Override
  public void updateDataSet() {
    List<ListingItem> newDataSet = createDataSet();
    dataSet.clear();
    dataSet.addAll(newDataSet);
  }

  @Override
  public final void notifyDataSetChanged() {
    updateDataSet();
    dataObservable.notifyChanged();
  }

  @Override
  public void registerDataObserver(DataObserver observer) {
    dataObservable.registerObserver(observer);
  }

  @Override
  public void unregisterDataObserver(DataObserver observer) {
    dataObservable.unregisterObserver(observer);
  }

  public void registerListingItemType(ListingItemType<VH> listingItemType) {
    itemTypeRegistry.put(listingItemType.getViewType(), listingItemType);
  }

  public ListingItemType<VH> getListingItemType(int viewType) {
    return itemTypeRegistry.get(viewType);
  }

  @Override
  public int getCount() {
    return dataSet.size();
  }

  @Override
  public int getViewType(int itemIndex) {
    ListingItem listingItem = dataSet.get(itemIndex);
    return listingItem.getListingItemType().getViewType();
  }

  @Override
  public Object getData(int itemIndex) {
    return dataSet.get(itemIndex).getData();
  }

  @Override
  public View getView(ViewGroup containerView, int viewType) {
    ListingItemType viewCreator = itemTypeRegistry.get(viewType);
    return viewCreator.createView(containerView);
  }

  @Override
  public VH getViewHolder(View itemView, int viewType) {
    ListingItemType<VH> viewCreator = itemTypeRegistry.get(viewType);
    return viewCreator.createViewHolder(itemView);
  }
}
