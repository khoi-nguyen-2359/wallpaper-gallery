package com.khoinguyen.apptemplate.listing.adapter;

import android.view.View;

import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.util.log.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 * <p/>
 * This adapter saves one viewholder for each item view created. Used in case item views are recycled.
 */
public abstract class RecycledListingAdapter<VH extends IViewHolder> extends PartitionedListingAdapter<VH> {
	protected Map<View, VH> mapViewHolderByView = new HashMap<>();
	private L log = L.get("RecycledListingAdapter");

	@Override
	public VH getViewHolder(View itemView, int viewType) {
		VH viewHolder = mapViewHolderByView.get(itemView);
		if (viewHolder == null) {
			viewHolder = super.getViewHolder(itemView, viewType);
			log.d("createViewHolder viewType=%s", viewType);
			mapViewHolderByView.put(itemView, viewHolder);
		}

		return viewHolder;
	}
}
