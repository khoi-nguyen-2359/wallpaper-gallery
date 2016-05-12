package com.khoinguyen.apptemplate.listing.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.ItemCreator;
import com.khoinguyen.apptemplate.listing.ItemViewHolder;
import com.khoinguyen.util.log.L;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by khoinguyen on 5/9/16.
 * <p/>
 * This adapter provides item views/viewholders based on item view type.
 */
public abstract class BaseListingViewAdapter<DATA> implements ListingViewAdapter {
  protected SparseArrayCompat<DATA> mapData = new SparseArrayCompat<>();
  protected Map<Object, ItemCreator> mapViewCreatorByViewType = new HashMap<>();
  private L log = L.get("BaseListingViewAdapter");

  public void registerViewCreator(ItemCreator viewCreator) {
    mapViewCreatorByViewType.put(viewCreator.getClass(), viewCreator);
  }

  public Set<Object> getAllViewTypes() {
    return mapViewCreatorByViewType.keySet();
  }

  @Override
  public View getView(ViewGroup containerView, Object viewType) {
    ItemCreator viewCreator = mapViewCreatorByViewType.get(viewType);
    return viewCreator.createView(containerView);
  }

  @Override
  public void bindData(View itemView, int itemIndex) {
    if (itemView == null) {
      return;
    }

    Object itemData = getData(itemIndex);
    Object viewType = getViewType(itemIndex);
    ItemViewHolder viewHolder = getViewHolder(itemView, viewType);
    if (viewHolder != null) {
      viewHolder.bind(itemData);
    }
  }

  @Override
  public DATA getData(int itemIndex) {
    DATA data = mapData.get(itemIndex);
    if (data == null) {
      data = createData(itemIndex);
      mapData.put(itemIndex, data);
    }

    return data;
  }

  public abstract DATA createData(int itemIndex);

  public ItemViewHolder getViewHolder(View itemView, Object viewType) {
    ItemCreator viewCreator = mapViewCreatorByViewType.get(viewType);
    return viewCreator.createViewHolder(itemView);
  }

  /**
   * Get the object that map to view creator for a specific position<br/>
   * Default implementation returns the first (if any) view type object and can be used for simple listing that has only one view type.
   *
   * @param itemIndex
   * @return
   */
  public Object getViewType(int itemIndex) {
    Iterator<Object> iterViewCreators = mapViewCreatorByViewType.keySet().iterator();
    if (!iterViewCreators.hasNext()) {
      return null;
    }

    return iterViewCreators.next();
  }
}
