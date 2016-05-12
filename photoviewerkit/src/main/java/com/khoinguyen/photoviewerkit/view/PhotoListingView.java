package com.khoinguyen.photoviewerkit.view;

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
import com.khoinguyen.apptemplate.listing.ItemCreator;
import com.khoinguyen.apptemplate.listing.ItemViewHolder;
import com.khoinguyen.apptemplate.listing.adapter.RecycledListingViewAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.data.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.data.DataStore;
import com.khoinguyen.photoviewerkit.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photoviewerkit.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;
import com.khoinguyen.apptemplate.listing.util.ListingRecyclerViewAdapter;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class PhotoListingView extends RecyclerView {
  protected StaggeredGridLayoutManager rcvLayoutMan;
  protected RecycledListingViewAdapter<PhotoDisplayInfo> listingAdapter;
  protected AdapterPhotoFinder photoFinder;

  protected LightEventBus eventBus;
  protected DataStore dataStore;
  protected RecyclerViewAdapter adapterPhotos;

  private L log = L.get("DefaultPhotoListingView");

  public PhotoListingView(Context context) {
    super(context);
    init();
  }

  public PhotoListingView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PhotoListingView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    Resources resources = getResources();
    final int nLayoutCol = 2;   //resources.getInteger(R.integer.photo_page_col);
    rcvLayoutMan = new StaggeredGridLayoutManager(nLayoutCol, StaggeredGridLayoutManager.VERTICAL);
    setLayoutManager(rcvLayoutMan);
    addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));

    adapterPhotos = new RecyclerViewAdapter();
    setAdapter(adapterPhotos);
  }

  public void setListingAdapter(RecycledListingViewAdapter<PhotoDisplayInfo> adapter) {
    this.listingAdapter = adapter;
    adapterPhotos.setListingAdapter(listingAdapter);
    adapterPhotos.notifyDataSetChanged();
  }

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
    ListingItemInfo lastSelectedItem = dataStore.getLastSelectedItem();
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();
    if (lastSelectedItem.getPhotoId() != null && lastSelectedItem.getPhotoId().equals(currentSelectedItem.getPhotoId())) {
      return;
    }

    unhighlightClickedItemView(getPhotoFinder().indexOf(lastSelectedItem.getPhotoId()));
    highlightClickedItemView(getPhotoFinder().indexOf(currentSelectedItem.getPhotoId()));
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
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();
    unhighlightClickedItemView(photoFinder.indexOf(currentSelectedItem.getPhotoId()));
  }

  public void setEventBus(LightEventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void setDataStore(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  /**
   * Created by khoinguyen on 12/22/14.
   */
  public class RecyclerViewAdapter extends ListingRecyclerViewAdapter {
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
      super.onBindViewHolder(viewHolder, position);
      log.d("DefaultPhotoListingView.childCount=%d", PhotoListingView.this.getChildCount());

      final PhotoDisplayInfo photoData = listingAdapter.getData(position);
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
  }

  private AdapterPhotoFinder getPhotoFinder() {
    if (photoFinder == null || photoFinder.getAdapter() != listingAdapter) {
      photoFinder = new AdapterPhotoFinder(listingAdapter);
    }

    return photoFinder;
  }

  private void resetItemSelectionInfo(PhotoDisplayInfo selectedPhotoItem) {
    if (selectedPhotoItem == null) {
      return;
    }

    ListingItemInfo lastSelectedItem = dataStore.getLastSelectedItem();
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();

    lastSelectedItem.setPhotoId("");
    lastSelectedItem.updateItemRect(new RectF());

    currentSelectedItem.setPhotoId(selectedPhotoItem.getPhotoId());
  }

  private void updateSelectedItemRect() {
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();
    int itemIndex = getPhotoFinder().indexOf(currentSelectedItem.getPhotoId());
    View v = rcvLayoutMan.findViewByPosition(itemIndex);
    RectF itemRect = createItemViewLocation(v);
    currentSelectedItem.updateItemRect(itemRect);
  }

  private void highlightClickedItemView(int itemIndex) {
    if (itemIndex < 0 || itemIndex >= listingAdapter.getCount()) {
      return;
    }

    View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
    itemView.setVisibility(View.INVISIBLE);
  }

  private void unhighlightClickedItemView(int itemIndex) {
    if (itemIndex < 0 || itemIndex >= listingAdapter.getCount()) {
      return;
    }

    View itemView = rcvLayoutMan.findViewByPosition(itemIndex);
    itemView.setVisibility(View.VISIBLE);
  }

  private void postPhotoListingItemClickEvent(PhotoDisplayInfo photoDisplayInfo) {
    RectF fullRect = new RectF(getX(), getY(), getWidth(), getHeight());   // stretch details itemView maximum at photokit size

    eventBus.post(new OnPhotoListingItemClick(photoDisplayInfo, fullRect));
  }

  public static class PhotoListingViewCreator implements ItemCreator {
    private LayoutInflater layoutInflater;

    @Override
    public View createView(ViewGroup container) {
      if (layoutInflater == null) {
        layoutInflater = LayoutInflater.from(container.getContext());
      }

      return layoutInflater.inflate(R.layout.photokit_photo_listing_item, container, false);
    }

    @Override
    public ItemViewHolder createViewHolder(View view) {
      return new PhotoListingItemViewHolder(view);
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
    public void bind(PhotoDisplayInfo data) {
      ivPhoto.setAspectRatio(1.5f);
      ivPhoto.setImageURI(Uri.parse(data.getLowResUrl()));
    }
  }
}
