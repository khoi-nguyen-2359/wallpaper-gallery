package com.khoinguyen.photokit.sample.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.eventbus.EventEmitter;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.PhotoListingViewBinder;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photokit.sample.event.Subscribe;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class DefaultPhotoListingView extends RecyclerView implements PhotoListingView {
    private RecyclerView.LayoutManager rcvLayoutMan;
    private RectF currentItemRect;
    private PhotoListingViewBinder binder;

    private EventEmitter eventEmitter = EventEmitter.getDefaultInstance();
    private PhotoListingItemAdapter adapterPhotos;
    private int lastHighlightedItemIndex;

    public DefaultPhotoListingView(Context context) {
        super(context);
        init();
    }

    public DefaultPhotoListingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultPhotoListingView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources resources = getResources();
        final int nLayoutCol = 2;   //resources.getInteger(R.integer.photo_page_col);
        rcvLayoutMan = new StaggeredGridLayoutManager(nLayoutCol, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(rcvLayoutMan);
        addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));

        adapterPhotos = new PhotoListingItemAdapter();
        setAdapter(adapterPhotos);
    }

    @Override
    public void setBinder(PhotoListingViewBinder binder) {
        this.binder = binder;
    }

    @Override
    public void notifyDataSetChanged() {
        adapterPhotos.notifyDataSetChanged();
    }

    private static RectF createItemViewLocation(View itemView) {
        int[] location = new int[2];
        itemView.getLocationInWindow(location);
        return new RectF(location[0], location[1], location[0] + itemView.getWidth(), location[1] + itemView.getHeight());
    }

    @Subscribe
    public void handlePhotoGalleryPageSelected(OnPhotoGalleryPageSelect event) {
        scrollToPosition(event.getItemIndex());
        changeSelectedItemHighlight(event.getItemIndex());
    }

    private void changeSelectedItemHighlight(int currentSelectedItem) {
        if (lastHighlightedItemIndex == currentSelectedItem) {
            return;
        }

        View lastSelectedItemView = rcvLayoutMan.findViewByPosition(lastHighlightedItemIndex);
        unhighlightClickItemView(lastSelectedItemView);
        View selectItemView = rcvLayoutMan.findViewByPosition(currentSelectedItem);
        highlightClickItemView(selectItemView, currentSelectedItem);
    }

    @Subscribe
    public void handlePhotoShrinkAnimationEnd(OnPhotoShrinkAnimationEnd event) {
        View itemView = rcvLayoutMan.findViewByPosition(lastHighlightedItemIndex);
        unhighlightClickItemView(itemView);
    }

    @Subscribe
    public void handlePhotoGalleryDragStart(OnPhotoGalleryDragStart event) {
        if (currentItemRect == null) {
            return; //todo: might need an emergency item location
        }

        View currentItemView = rcvLayoutMan.findViewByPosition(event.getCurrentItem());
        RectF currentItemViewLocation = createItemViewLocation(currentItemView);
        currentItemRect.set(currentItemViewLocation);
    }

    /**
     * Created by khoinguyen on 12/22/14.
     */
    public class PhotoListingItemAdapter extends Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(binder.getItemView(viewGroup, i));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            binder.bindItemData(viewHolder.itemView, position);
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    postPhotoListingItemClickEvent(v);
                    highlightClickItemView(v, viewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return binder == null ? 0 : binder.getItemCount();
        }
    }

    private void highlightClickItemView(View v, int position) {
        if (v == null) {
            return;
        }

//        com.khoinguyen.photokit.ItemViewHolder viewHolder = binder.getDefaultPhotoListingItemViewHolder(v);
//        viewHolder.getPhotoView().setVisibility(View.INVISIBLE);
        lastHighlightedItemIndex = position;
    }

    private void unhighlightClickItemView(View v) {
        if (v == null) {
            return;
        }

//        DefaultPhotoListingViewBinder.DefaultPhotoListingItemItemViewHolder viewHolder = binder.getDefaultPhotoListingItemViewHolder(v);
//        viewHolder.getPhotoView().setVisibility(View.VISIBLE);
    }

    private void postPhotoListingItemClickEvent(View v) {
        int position = getChildAdapterPosition(v);
        currentItemRect = createItemViewLocation(v);
        RectF fullRect = new RectF(getX(), getY(), getWidth(), getHeight());   // stretch details itemView maximum at photokit size

        eventEmitter.post(new OnPhotoListingItemClick(currentItemRect, fullRect, binder.getPhotoDisplayInfo(position), position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
