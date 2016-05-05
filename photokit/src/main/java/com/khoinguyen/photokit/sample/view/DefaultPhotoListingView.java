package com.khoinguyen.photokit.sample.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.photokit.ItemViewHolder;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.eventbus.EventEmitter;
import com.khoinguyen.photokit.sample.binder.DefaultBasePhotoListingViewBinder;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photokit.sample.event.Subscribe;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.sample.model.PhotoListingItemTrackingInfo;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class DefaultPhotoListingView extends RecyclerView implements PhotoListingView<DefaultBasePhotoListingViewBinder> {
    protected RecyclerView.LayoutManager rcvLayoutMan;
    protected DefaultBasePhotoListingViewBinder binder;

    protected EventEmitter eventEmitter = EventEmitter.getDefaultInstance();
    protected PhotoListingItemAdapter adapterPhotos;

    protected PhotoListingItemTrackingInfo currentSelectedItemInfo;
    protected PhotoListingItemTrackingInfo lastSelectedItemInfo;

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

        currentSelectedItemInfo = new PhotoListingItemTrackingInfo();
        lastSelectedItemInfo = new PhotoListingItemTrackingInfo();

        adapterPhotos = new PhotoListingItemAdapter();
        setAdapter(adapterPhotos);
    }

    @Override
    public void setBinder(DefaultBasePhotoListingViewBinder binder) {
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
    }

    private void changeSelectedItemHighlight() {
        if (lastSelectedItemInfo.getItemIndex() == currentSelectedItemInfo.getItemIndex()) {
            return;
        }

        unhighlightClickedItemView(lastSelectedItemInfo.getItemIndex());
        highlightClickedItemView(currentSelectedItemInfo.getItemIndex());
    }

    @Subscribe
    public void onPhotoGalleryDragStart(OnPhotoGalleryDragStart event) {
        onChangeSelectedItemHighlight();
    }

    @Subscribe
    public void onPhotoShrinkAnimWillStart(OnPhotoShrinkAnimationWillStart event) {
        onChangeSelectedItemHighlight();
    }

    private void onChangeSelectedItemHighlight() {
        updateSelectedItemRect();
        changeSelectedItemHighlight();
    }

    @Subscribe
    public void handlePhotoShrinkAnimationEnd(OnPhotoShrinkAnimationEnd event) {
        unhighlightClickedItemView(currentSelectedItemInfo.getItemIndex());
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
                    resetItemSelectionInfo(viewHolder.getAdapterPosition());
                    updateSelectedItemRect();
                    postPhotoListingItemClickEvent();
                }
            });
        }

        @Override
        public int getItemCount() {
            return binder == null ? 0 : binder.getItemCount();
        }
    }

    private void resetItemSelectionInfo(int clickedItemIndex) {
        lastSelectedItemInfo.reset();

        currentSelectedItemInfo.setItemIndex(clickedItemIndex);
        currentSelectedItemInfo.setItemPhoto(binder.getPhotoDisplayInfo(clickedItemIndex));
    }

    private void updateSelectedItemRect() {
        View v = rcvLayoutMan.findViewByPosition(currentSelectedItemInfo.getItemIndex());
        RectF itemRect = createItemViewLocation(v);
        currentSelectedItemInfo.updateItemRect(itemRect);
    }

    private void highlightClickedItemView(int itemIndex) {
        View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
        itemView.setVisibility(View.INVISIBLE);
    }

    private void unhighlightClickedItemView(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= adapterPhotos.getItemCount()) {
            return;
        }

        View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
        itemView.setVisibility(View.VISIBLE);
    }

    private void postPhotoListingItemClickEvent() {
        RectF fullRect = new RectF(getX(), getY(), getWidth(), getHeight());   // stretch details itemView maximum at photokit size

        eventEmitter.post(new OnPhotoListingItemClick(currentSelectedItemInfo, fullRect));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Created by khoinguyen on 4/29/16.
     */
    public abstract static class Binder extends DefaultBasePhotoListingViewBinder {
        protected LayoutInflater layoutInflater;

        @Override
        public View getItemView(ViewGroup container, int itemIndex) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(container.getContext());
            }

            return layoutInflater.inflate(R.layout.photokit_photo_listing_item, container, false);
        }

        @Override
        public ItemViewHolder<PhotoDisplayInfo> createPhotoDisplayItemViewHolder(View itemView) {
            return new PhotoListingItemViewHolder(itemView);
        }
    }

    public static class PhotoListingItemViewHolder extends ItemViewHolder<PhotoDisplayInfo> {
        protected final SimpleDraweeView ivPhoto;

        public PhotoListingItemViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            GenericDraweeHierarchy photoHierarchy = GenericDraweeHierarchyBuilder.newInstance(itemView.getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFadeDuration(0)
                    .build();
            ivPhoto.setHierarchy(photoHierarchy);
        }

        @Override
        public void bindItemData(int itemIndex, PhotoDisplayInfo data) {
            super.bindItemData(itemIndex, data);

            ivPhoto.setAspectRatio(1.5f);
            ivPhoto.setImageURI(Uri.parse(data.getLowResUrl()));
        }
    }
}
