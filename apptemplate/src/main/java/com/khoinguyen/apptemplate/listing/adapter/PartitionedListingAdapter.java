package com.khoinguyen.apptemplate.listing.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.IViewHolder;
import com.khoinguyen.apptemplate.listing.BaseItemCreator;
import com.khoinguyen.apptemplate.listing.ItemPart;
import com.khoinguyen.util.log.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 5/9/16.
 * <p/>
 * This adapter provides item views/viewholders based on item view type.
 */
public abstract class PartitionedListingAdapter<VH extends IViewHolder> implements ListingAdapter<VH> {
  protected SparseArrayCompat<BaseItemCreator<VH>> mapViewCreatorByViewType = new SparseArrayCompat<>();
  protected DataObservable dataObservable = new DataObservable();

  private L log = L.get("PartitionedListingAdapter");

  protected final List<ItemPart> dataSet = new ArrayList<>();

  protected abstract List<ItemPart> createDataSet();

  @Override
  public void updateDataSet() {
    dataSet.clear();
    dataSet.addAll(createDataSet());
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

  public void registerViewCreator(BaseItemCreator<VH> viewCreator) {
    mapViewCreatorByViewType.put(viewCreator.getViewType(), viewCreator);
  }

  @Override
  public int getCount() {
    return dataSet.size();
  }

  @Override
  public int getViewType(int itemIndex) {
    ItemPart part = dataSet.get(itemIndex);
    return part.getViewType();
  }

  @Override
  public Object getData(int itemIndex) {
    return dataSet.get(itemIndex).getData();
  }

  @Override
  public View getView(ViewGroup containerView, int viewType) {
    BaseItemCreator viewCreator = mapViewCreatorByViewType.get(viewType);
    return viewCreator.createView(containerView);
  }

  @Override
  public VH getViewHolder(View itemView, int viewType) {
    BaseItemCreator<VH> viewCreator = mapViewCreatorByViewType.get(viewType);
    return viewCreator.createViewHolder(itemView);
  }
}
