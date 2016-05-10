package com.khoinguyen.photokit.util;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.photokit.adapter.BaseListingViewAdapter;

/**
 * Created by khoinguyen on 5/10/16.
 */
public class ListingPagerViewAdapter extends PagerAdapter {
    private BaseListingViewAdapter listingViewAdapter;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = listingViewAdapter.getView(container, listingViewAdapter.getViewType(position));
        listingViewAdapter.bindData(itemView, position);
        container.addView(itemView);
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

    public void setListingViewAdapter(BaseListingViewAdapter listingViewAdapter) {
        this.listingViewAdapter = listingViewAdapter;
    }
}
