package com.khoinguyen.apptemplate.listing.util;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.adapter.DataObserver;
import com.khoinguyen.apptemplate.listing.adapter.ListingAdapter;

/**
 * Created by khoinguyen on 5/10/16.
 */
public class PagerListingAdapter extends PagerAdapter {
  private ListingAdapter listingViewAdapter;
  private DataObserver dataObserver = new DataObserver() {
    @Override
    public void onChanged() {
      super.onChanged();
      notifyDataSetChanged();
    }
  };

  @Override
  public void notifyDataSetChanged() {
    if (listingViewAdapter != null) {
      listingViewAdapter.updateDataSet();
    }

    super.notifyDataSetChanged();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    int viewType = listingViewAdapter.getViewType(position);
    View itemView = listingViewAdapter.getView(container, viewType);
    IViewHolder viewHolder = listingViewAdapter.getViewHolder(itemView, viewType);

    container.addView(itemView);

    Object data = listingViewAdapter.getData(position);
    viewHolder.bind(data);

    return itemView;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    if (object instanceof View) {
      container.removeView((View) object);
    }
  }

  @Override
  public int getCount() {
    return listingViewAdapter == null ? 0 : listingViewAdapter.getCount();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  public void setListingViewAdapter(ListingAdapter listingViewAdapter) {
    if (this.listingViewAdapter != null) {
      this.listingViewAdapter.unregisterDataObserver(dataObserver);
    }

    if (listingViewAdapter != null) {
      listingViewAdapter.registerDataObserver(dataObserver);
    }

    this.listingViewAdapter = listingViewAdapter;
  }
}
