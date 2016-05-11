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
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.adapter.ListingViewHolder;
import com.khoinguyen.photokit.adapter.RecycledListingViewAdapter;
import com.khoinguyen.photokit.adapter.ViewCreator;
import com.khoinguyen.photokit.eventbus.LightEventBus;
import com.khoinguyen.photokit.eventbus.Subscribe;
import com.khoinguyen.photokit.sample.adapter.AdapterPhotoFinder;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.util.ListingRecyclerViewAdapter;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class DefaultPhotoListingView extends RecyclerView implements PhotoListingView<RecycledListingViewAdapter<PhotoDisplayInfo>> {
  protected StaggeredGridLayoutManager rcvLayoutMan;
  protected RecycledListingViewAdapter<PhotoDisplayInfo> photoAdapter;
  protected AdapterPhotoFinder photoFinder;

  protected LightEventBus eventEmitter = LightEventBus.getDefaultInstance();
  protected RecyclerViewAdapter adapterPhotos;

  protected PhotoListingItemTrackingInfo currentSelectedItemInfo;
  protected PhotoListingItemTrackingInfo lastSelectedItemInfo;
  private L log = L.get("DefaultPhotoListingView");

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

    adapterPhotos = new RecyclerViewAdapter();
    setAdapter(adapterPhotos);
  }

  public void setAdapter(RecycledListingViewAdapter<PhotoDisplayInfo> adapter) {
    this.photoAdapter = adapter;
    adapterPhotos.setListingAdapter(photoAdapter);
    adapterPhotos.notifyDataSetChanged();
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
    if (lastSelectedItemInfo.getPhotoId() != null && lastSelectedItemInfo.getPhotoId().equals(currentSelectedItemInfo.getPhotoId())) {
      return;
    }

    unhighlightClickedItemView(getPhotoFinder().indexOf(lastSelectedItemInfo.getPhotoId()));
    highlightClickedItemView(getPhotoFinder().indexOf(currentSelectedItemInfo.getPhotoId()));
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
    unhighlightClickedItemView(photoFinder.indexOf(currentSelectedItemInfo.getPhotoId()));
  }

  /**
   * Created by khoinguyen on 12/22/14.
   */
  public class RecyclerViewAdapter extends ListingRecyclerViewAdapter {
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
      super.onBindViewHolder(viewHolder, position);
      log.d("DefaultPhotoListingView.childCount=%d", DefaultPhotoListingView.this.getChildCount());

      final PhotoDisplayInfo photoData = photoAdapter.getData(position);
      if (photoData != null) {
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            resetItemSelectionInfo(photoData);
            updateSelectedItemRect();
            postPhotoListingItemClickEvent(photoData);
          }
        });
      }
    }

    @Override
    public int getItemCount() {
      return photoAdapter == null ? 0 : photoAdapter.getCount();
    }
  }

  private AdapterPhotoFinder getPhotoFinder() {
    if (photoFinder == null || photoFinder.getAdapter() != photoAdapter) {
      photoFinder = new AdapterPhotoFinder(photoAdapter);
    }

    return photoFinder;
  }

  private void resetItemSelectionInfo(PhotoDisplayInfo selectedPhotoItem) {
    if (selectedPhotoItem == null) {
      return;
    }

    lastSelectedItemInfo.setPhotoId("");
    lastSelectedItemInfo.updateItemRect(new RectF());

    currentSelectedItemInfo.setPhotoId(selectedPhotoItem.getPhotoId());
  }

  private void updateSelectedItemRect() {
    int itemIndex = getPhotoFinder().indexOf(currentSelectedItemInfo.getPhotoId());
    View v = rcvLayoutMan.findViewByPosition(itemIndex);
    RectF itemRect = createItemViewLocation(v);
    currentSelectedItemInfo.updateItemRect(itemRect);
  }

  private void highlightClickedItemView(int itemIndex) {
    if (itemIndex < 0 || itemIndex >= photoAdapter.getCount()) {
      return;
    }

    View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
    itemView.setVisibility(View.INVISIBLE);
  }

  private void unhighlightClickedItemView(int itemIndex) {
    if (itemIndex < 0 || itemIndex >= photoAdapter.getCount()) {
      return;
    }

    View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
    itemView.setVisibility(View.VISIBLE);
  }

  private void postPhotoListingItemClickEvent(PhotoDisplayInfo photoDisplayInfo) {
    RectF fullRect = new RectF(getX(), getY(), getWidth(), getHeight());   // stretch details itemView maximum at photokit size

    eventEmitter.post(new OnPhotoListingItemClick(photoDisplayInfo, currentSelectedItemInfo, fullRect));
  }

  public static class PhotoListingViewCreator implements ViewCreator {
    private LayoutInflater layoutInflater;

    @Override
    public View createView(ViewGroup container) {
      if (layoutInflater == null) {
        layoutInflater = LayoutInflater.from(container.getContext());
      }

      return layoutInflater.inflate(R.layout.photokit_photo_listing_item, container, false);
    }

    @Override
    public ListingViewHolder createViewHolder(View view) {
      return new PhotoListingItemViewHolder(view);
    }
  }

  public static class PhotoListingItemViewHolder extends ListingViewHolder<PhotoDisplayInfo> {
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
    public void bind(PhotoDisplayInfo data) {
      ivPhoto.setAspectRatio(1.5f);
      ivPhoto.setImageURI(Uri.parse(data.getLowResUrl()));
    }
  }

  /**
   * Created by khoinguyen on 5/4/16.
   */
  public static class PhotoListingItemTrackingInfo {
    private RectF itemRect = new RectF();
    private String photoId;

    public String getPhotoId() {
      return photoId;
    }

    public RectF getItemRect() {
      return itemRect;
    }

    public void updateItemRect(RectF itemRect) {
      this.itemRect.set(itemRect);
    }

    public void setPhotoId(String photoId) {
      this.photoId = photoId;
    }
  }
}
