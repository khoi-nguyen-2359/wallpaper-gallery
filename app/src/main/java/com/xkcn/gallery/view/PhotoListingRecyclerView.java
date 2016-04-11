package com.xkcn.gallery.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListingItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.OnPhotoListItemClicked;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class PhotoListingRecyclerView extends RecyclerView implements PhotoListingView {
    private PhotoListingItemAdapter adapterPhotos;

    public PhotoListingRecyclerView(Context context) {
        super(context);
        init();
    }

    public PhotoListingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoListingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources resources = getResources();
        int nLayoutCol = resources.getInteger(R.integer.photo_page_col);
        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nLayoutCol, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(rcvLayoutMan);
        addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));
    }

    @Override
    public void displayPhotoData(List<PhotoDetails> photos) {
        if (adapterPhotos == null) {
            adapterPhotos = new PhotoListingItemAdapter(getContext());
            adapterPhotos.setOnItemViewClicked(onItemViewClicked);
            setAdapter(adapterPhotos);
        }

        adapterPhotos.setDataPhotos(photos);
        adapterPhotos.notifyDataSetChanged();
    }

    private PhotoDetails getPhotoDetails(int position) {
        if (adapterPhotos == null) {
            return null;
        }

        List<PhotoDetails> allPhotos = adapterPhotos.getDataPhotos();
        if (allPhotos == null || allPhotos.isEmpty() || position < 0 || position >= allPhotos.size()) {
            return null;
        }

        return allPhotos.get(position);
    }

    private OnClickListener onItemViewClicked = new OnClickListener() {
        @Override
        public void onClick(View itemView) {
            PhotoListingItemAdapter.ViewHolder viewHolder = (PhotoListingItemAdapter.ViewHolder) getChildViewHolder(itemView);
            int position = getChildAdapterPosition(itemView);
            EventBus.getDefault().post(new OnPhotoListItemClicked(position, viewHolder, getPhotoDetails(position)));
        }
    };
}
