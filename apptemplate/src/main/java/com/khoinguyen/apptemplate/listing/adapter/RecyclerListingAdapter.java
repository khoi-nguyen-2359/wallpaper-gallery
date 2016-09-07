package com.khoinguyen.apptemplate.listing.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 5/10/16.
 */
public class RecyclerListingAdapter extends RecyclerView.Adapter<RecyclerListingViewHolder> {
	private IListingAdapter<RecyclerListingViewHolder> listingAdapter;
	private DataObserver dataObserver = new DataObserver() {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}
	};

	private L log = L.get("RecyclerListingAdapter");

	public void setListingAdapter(IListingAdapter<RecyclerListingViewHolder> listingAdapter) {
		if (this.listingAdapter != null) {
			this.listingAdapter.unregisterDataObserver(dataObserver);
		}

		if (listingAdapter != null) {
			listingAdapter.registerDataObserver(dataObserver);
		}

		this.listingAdapter = listingAdapter;
	}

	@Override
	public RecyclerListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return listingAdapter.getViewHolder(listingAdapter.getView(parent, viewType), viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerListingViewHolder holder, int position) {
		holder.bind(listingAdapter.getData(position));
	}

	@Override
	public int getItemCount() {
		return listingAdapter == null ? 0 : listingAdapter.getCount();
	}

	@Override
	public int getItemViewType(int position) {
		return listingAdapter.getViewType(position);
	}
}
