package com.khoinguyen.apptemplate.listing.adapter;

import android.database.Observable;
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

  protected abstract List<ItemPart> updateDataSet();

  public void doUpdateDataSet() {
    dataSet.clear();
    dataSet.addAll(updateDataSet());
  }

  public final void notifyDataSetChanged() {
    doUpdateDataSet();
    dataObservable.notifyChanged();
  }

  public void registerDataObserver(DataObserver observer) {
    dataObservable.registerObserver(observer);
  }

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

  public abstract static class DataObserver {
    public void onChanged() {
    }
  }

  public class DataObservable extends Observable<DataObserver> {
    public boolean hasObservers() {
      return !mObservers.isEmpty();
    }

    public void notifyChanged() {
      for (int i = mObservers.size() - 1; i >= 0; i--) {
        mObservers.get(i).onChanged();
      }
    }
  }
}
