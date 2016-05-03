package com.khoinguyen.photokit.sample.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.sample.binder.DefaultPhotoListingViewBinder;
import com.khoinguyen.photokit.eventbus.EventEmitter;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.PhotoListingViewBinder;
import com.khoinguyen.photokit.sample.event.Subscribe;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.khoinguyen.ui.UiUtils;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class DefaultPhotoListingView extends RecyclerView implements PhotoListingView {
    private RecyclerView.LayoutManager rcvLayoutMan;
    private RectF currentItemRect;
    private DefaultPhotoListingViewBinder binder;

    private EventEmitter eventEmitter = EventEmitter.getDefaultInstance();
    private PhotoListingItemAdapter adapterPhotos;

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
        this.binder = (DefaultPhotoListingViewBinder) binder;
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

    private View.OnClickListener onItemViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View itemView) {
            int position = getChildAdapterPosition(itemView);
            currentItemRect = createItemViewLocation(itemView);
            DisplayMetrics displayMetrics = UiUtils.getDisplayMetrics(getContext());
            RectF fullRect = new RectF(getX(), getY(), getWidth(), getHeight());   // stretch details view maximum at photokit size

            eventEmitter.post(new OnPhotoListingItemClick(currentItemRect, fullRect, binder.getPhotoDisplayInfo(position), position));
        }
    };

    @Subscribe
    public void handlePhotoGalleryPageSelected(OnPhotoGalleryPageSelect event) {
        scrollToPosition(event.getPosition());
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
            return new ViewHolder(binder.createItemView(viewGroup, i));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            binder.bindItemData(viewHolder.itemView, position);
            viewHolder.itemView.setOnClickListener(onItemViewClicked);
        }

        @Override
        public int getItemCount() {
            return binder == null ? 0 : binder.getItemCount();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
