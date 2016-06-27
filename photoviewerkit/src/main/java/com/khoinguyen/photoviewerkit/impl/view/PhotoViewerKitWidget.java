package com.khoinguyen.photoviewerkit.impl.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.pageable.IPageableListingView;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.apptemplate.listing.pageable.PageableListingViewCollection;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoListingView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoTransitionView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoViewerKitWidget extends RelativeLayout implements IPhotoViewerKitWidget<SharedData> {
  protected LightEventBus eventBus;

  protected IPhotoGalleryView<SharedData> photoGalleryView;
  protected IPhotoListingView<SharedData, ? extends IViewHolder> photoListingView;
  protected View photoActionButton;

  protected PageableListingViewCollection pageableListingViews = new PageableListingViewCollection();

  protected IPhotoTransitionView<SharedData> transitDraweeView;
  protected PhotoBackdropView transitBackdrop;

  protected TransitState currentTransitState = TransitState.LISTING;
  protected SharedData sharedData;

  private IPhotoViewerKitWidget.PagingListener pagingListener;

  public PhotoViewerKitWidget(Context context) {
    super(context);
    init();
  }

  public PhotoViewerKitWidget(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
    readAttrs(attrs);
  }

  public PhotoViewerKitWidget(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
    readAttrs(attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PhotoViewerKitWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
    readAttrs(attrs);
  }

  private void init() {
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    initViews();
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    registerEvents();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    unregisterEvents();
  }

  private void readAttrs(AttributeSet attrs) {
    if (attrs == null) {
      return;
    }
  }

  private void initViews() {
    photoListingView = (IPhotoListingView<SharedData, ? extends IViewHolder>) findViewById(R.id.photokit_photo_listing);
    photoGalleryView = (IPhotoGalleryView<SharedData>) findViewById(R.id.photokit_photo_gallery);

    transitDraweeView = (PhotoTransitionView) findViewById(R.id.photokit_transition_photo);
    transitBackdrop = (PhotoBackdropView) findViewById(R.id.photokit_transition_backdrop);

    photoListingView.attach(this);
    photoGalleryView.attach(this);
    transitDraweeView.attach(this);

    buildPageableListingViews();
  }

  private void buildPageableListingViews() {
    pageableListingViews.clear();
    pageableListingViews.add(photoListingView);
    pageableListingViews.add(photoGalleryView);
  }

  private OnClickListener onActionButtonClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {

    }
  };

  @Subscribe
  public void handleOnPhotoShrinkAnimationStart(OnPhotoShrinkAnimationStart event) {
    currentTransitState = TransitState.TO_LISTING;
  }

  @Subscribe
  public void handleOnPhotoShrinkAnimationEnd(OnPhotoShrinkAnimationEnd event) {
    currentTransitState = TransitState.LISTING;
  }

  @Subscribe
  public void handleOnPhotoRevealAnimStart(OnPhotoRevealAnimationStart event) {
    currentTransitState = TransitState.TO_GALLERY;
  }

  @Subscribe
  public void handleOnPhotoRevealAnimEnd(OnPhotoRevealAnimationEnd event) {
    currentTransitState = TransitState.GALLERY;
  }

  public TransitState getTransitionState() {
    return currentTransitState;
  }

  public void registerEvents() {
    eventBus.register(photoGalleryView);
    eventBus.register(photoListingView);
    eventBus.register(transitDraweeView);
    eventBus.register(transitBackdrop);
    eventBus.register(this);
  }

  public void unregisterEvents() {
    eventBus.unregister(photoGalleryView);
    eventBus.unregister(photoListingView);
    eventBus.unregister(transitBackdrop);
    eventBus.unregister(transitDraweeView);
    eventBus.unregister(this);
  }

  public IEventBus getEventBus() {
    return eventBus == null ? eventBus = new LightEventBus() : eventBus;
  }

  public SharedData getSharedData() {
    return sharedData == null ? sharedData = new SharedData() : sharedData;
  }

  @Override
  public void enablePaging() {
    pageableListingViews.enablePaging();
  }

  @Override
  public boolean handleBackPress() {
    if (currentTransitState == TransitState.GALLERY) {
      ((View) photoGalleryView).setVisibility(View.GONE);
      transitDraweeView.startShrinkAnimation(new RectF(0, 0, getWidth(), getHeight()));
      return true;
    }

    return false;
  }

  public void setPagingListener(PagingListener pagingListener) {
    this.pagingListener = pagingListener;
  }

  @Override
  public void onPagingNext(IPageableListingView component) {
    if (pagingListener != null) {
      pagingListener.onPagingNext(this);
    }
  }

  @Override
  public void openGallery(ListingItemInfo currentActiveItem) {
    transitDraweeView.show();
    transitDraweeView.dislayPhoto(currentActiveItem.getPhoto());
    Rect fullRect = new Rect();
    getDrawingRect(fullRect);
    L.get().d("openGallery fullrect=%s", fullRect);
    transitDraweeView.startRevealAnimation(currentActiveItem.getItemRect(), new RectF(fullRect));
  }

  // // TODO: 6/17/16 move this into SharedData
  enum TransitState {
    LISTING,
    TO_GALLERY,
    GALLERY,
    TO_LISTING
  }
}
