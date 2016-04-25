package com.khoinguyen.photokit.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.adapter.PhotoListingItemAdapter;
import com.khoinguyen.photokit.data.model.PhotoDetails;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;

import java.util.List;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class PhotoListingRecyclerView extends RecyclerView implements PhotoListingView {
    private PhotoListingItemAdapter adapterPhotos;
    private LayoutManager rcvLayoutMan;

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
        final int nLayoutCol = 2;//resources.getInteger(R.integer.photo_page_col);
        rcvLayoutMan = new StaggeredGridLayoutManager(nLayoutCol, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(rcvLayoutMan);
        addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));
    }

    @Override
    public void populatePhotoData(List<PhotoDetails> photos) {
        if (adapterPhotos == null) {
            adapterPhotos = new PhotoListingItemAdapter(getContext());
            adapterPhotos.setOnItemViewClicked(onItemViewClicked);
            setAdapter(adapterPhotos);
        }

        adapterPhotos.setDataPhotos(photos);
        adapterPhotos.notifyDataSetChanged();
    }

    @Override
    public void displayPhotoItem(int position) {
        scrollToPosition(position);
    }

    @Override
    public View getPhotoItemView(int position) {
        return rcvLayoutMan.findViewByPosition(position);
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

    private View.OnClickListener onItemViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View itemView) {
            PhotoListingItemAdapter.ViewHolder viewHolder = (PhotoListingItemAdapter.ViewHolder) getChildViewHolder(itemView);
            int position = getChildAdapterPosition(itemView);
            int[] location = new int[2];
            itemView.getLocationInWindow(location);
            RectF startRect = new RectF(location[0], location[1], location[0] + itemView.getWidth(), location[1] + itemView.getHeight());
//            EventBus.getDefault().post(new OnPhotoListItemClicked(position, getPhotoDetails(position), startRect));
        }
    };
}
