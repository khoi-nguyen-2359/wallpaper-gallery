package com.khoinguyen.photoviewerkit.impl.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
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
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.adapter.RecyclerListingAdapter;
import com.khoinguyen.apptemplate.listing.item.RecyclerListingViewHolder;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoListingItemActivate;
import com.khoinguyen.photoviewerkit.impl.util.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoListingView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.impl.util.RecyclerViewPagingListener;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class PhotoListingView extends RecyclerView implements IPhotoListingView<SharedData, RecyclerListingViewHolder> {
  private static final int PAGING_OFFSET = 10;
  protected StaggeredGridLayoutManager rcvLayoutMan;
  protected IListingAdapter<RecyclerListingViewHolder> listingAdapter;
  protected AdapterPhotoFinder photoFinder;

  protected IEventBus eventBus;
  protected SharedData sharedData;
  protected RecyclerViewAdapter adapterPhotos;

  private L log = L.get("DefaultPhotoListingView");
  protected IPhotoViewerKitWidget<SharedData> photoKitWidget;

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

    addOnScrollListener(rcvPagingListener);
  }

  public void setPhotoAdapter(IListingAdapter<RecyclerListingViewHolder> adapter) {
    this.listingAdapter = adapter;
    adapterPhotos.setListingAdapter(listingAdapter);
    updatePhotoFinder();
  }

  /**
   * create photo finder if not yet created or listing adapter has changed.
   */
  private void updatePhotoFinder() {
    if (photoFinder == null || photoFinder.getAdapter() != listingAdapter) {
      photoFinder = new AdapterPhotoFinder(listingAdapter);
    }
  }

  private static RectF createItemViewLocation(View itemView) {
    // // TODO: 6/28/16 what if itemView is null, which is in case that the requested photo item hasn't been laid out as view. Should return a rect to be ignored.
    if (itemView != null) {
      return new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());//new RectF(location., location[1], location[0] + itemView.getWidth(), location[1] + itemView.getHeight());
    }

    return new RectF();
  }

  @Subscribe
  public void handlePhotoGalleryPageSelected(OnPhotoGalleryPhotoSelect event) {
    scrollToPosition(event.getItemIndex());
  }

  private void changeActiveItemHighlight() {
    if (!sharedData.hasActiveItemChanged()) {
      return;
    }

    String lastActivePhotoId = sharedData.getLastActiveItem().getPhotoId();
    String currentActivePhotoId = sharedData.getCurrentActiveItem().getPhotoId();
    unhighlightClickedItemView(photoFinder.indexOf(lastActivePhotoId));
    highlightClickedItemView(photoFinder.indexOf(currentActivePhotoId));
  }

  @Subscribe
  public void onPhotoGalleryDragStart(OnPhotoGalleryDragStart event) {
    toggleActiveItems();
  }

  @Override
  public void toggleActiveItems() {
    updateActiveItemRect();
    changeActiveItemHighlight();
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) throws UnsupportedOperationException {
    sharedData = widget.getSharedData();
    eventBus = widget.getEventBus();
    photoKitWidget = widget;
  }

  @Override
  public void enablePaging() {
    rcvPagingListener.setEnable(true);
  }

  /**
   * Created by khoinguyen on 12/22/14.
   */
  public class RecyclerViewAdapter extends RecyclerListingAdapter {
    @Override
    public void onBindViewHolder(final RecyclerListingViewHolder viewHolder, int position) {
      super.onBindViewHolder(viewHolder, position);

      //// TODO: 6/27/16 what if the itemView has already set onClickListener before?
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            int position = rcvLayoutMan.getPosition(v);
            photoKitWidget.revealGallery(position);

            eventBus.post(new OnPhotoListingItemActivate());
          }
        });
      }
  }

  @Override
  public void activatePhotoItem(int position) {
    PhotoDisplayInfo photoData = photoFinder.getPhoto(position);
    if (photoData == null) {
      return;
    }

    sharedData.resetActiveItemInfo(photoData);
    updateActiveItemRect();
  }

  private void updateActiveItemRect() {
    ListingItemInfo currentSelectedItem = sharedData.getCurrentActiveItem();
    int itemIndex = photoFinder.indexOf(currentSelectedItem.getPhotoId());
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

  public static class PhotoItemType extends ListingItemType<RecyclerListingViewHolder> {
    private LayoutInflater layoutInflater;

    public PhotoItemType(int viewType) {
      super(viewType);
    }

    @Override
    public View createView(ViewGroup container) {
      if (layoutInflater == null) {
        layoutInflater = LayoutInflater.from(container.getContext());
      }

      return layoutInflater.inflate(R.layout.photokit_photo_listing_item, container, false);
    }

    @Override
    public RecyclerListingViewHolder createViewHolder(View view) {
      return new PhotoListingItemViewHolder(view);
    }
  }

  public static class PhotoListingItemViewHolder extends RecyclerListingViewHolder<PhotoDisplayInfo> {
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
      ivPhoto.setImageURI(data.getLowResUri());
    }
  }

  private RecyclerViewPagingListener rcvPagingListener = new RecyclerViewPagingListener(PAGING_OFFSET) {
    @Override
    public void onNext(RecyclerView recyclerView) {
        photoKitWidget.onPagingNext(PhotoListingView.this);
      }
  };
}
