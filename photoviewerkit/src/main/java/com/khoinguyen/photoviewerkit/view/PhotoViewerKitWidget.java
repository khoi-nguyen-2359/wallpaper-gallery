package com.khoinguyen.photoviewerkit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.khoinguyen.apptemplate.listing.adapter.BaseListingViewAdapter;
import com.khoinguyen.apptemplate.listing.adapter.RecycledListingViewAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.data.DataStore;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoViewerKitWidget extends RelativeLayout {
  protected LightEventBus eventBus;

  protected PhotoTransitionView transitDraweeView;
  protected PhotoBackdropView transitBackdrop;
  protected PhotoGalleryView photoGalleryView;
  protected PhotoListingView photoListingView;
  protected TransitionState currentTransitionState = TransitionState.LISTING;
  protected DataStore dataStore;

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

  public void setAdapters(RecycledListingViewAdapter<PhotoDisplayInfo> listingBinder, BaseListingViewAdapter<PhotoDisplayInfo> galleryBinder) {
    photoListingView.setListingAdapter(listingBinder);
    photoGalleryView.setListingAdapter(galleryBinder);
  }

  private void readAttrs(AttributeSet attrs) {
    if (attrs == null) {
      return;
    }
  }

  private void initViews() {
    photoGalleryView = (PhotoGalleryView) findViewById(R.id.photokit_pager_photo_gallery);
    transitDraweeView = (PhotoTransitionView) findViewById(R.id.photokit_transition_photo);
    transitBackdrop = (PhotoBackdropView) findViewById(R.id.photokit_transition_backdrop);
    photoListingView = (PhotoListingView) findViewById(R.id.photokit_photo_listing);

    photoListingView.eventBus = getEventBus();
    photoGalleryView.eventBus = getEventBus();
    transitDraweeView.eventBus = getEventBus();

    photoListingView.dataStore = getDataStore();
    photoGalleryView.dataStore = getDataStore();
    transitDraweeView.dataStore = getDataStore();
  }

  /**
   * @return False means no handling happens
   */
  public boolean handleBackPressed() {
    if (currentTransitionState == TransitionState.DETAILS) {
      ((View) photoGalleryView).setVisibility(View.GONE);
      transitDraweeView.startShrinkAnimation(new RectF(0, 0, getWidth(), getHeight()));
      return true;
    }

    return false;
  }

  @Subscribe
  public void handleOnPhotoShrinkAnimationStart(OnPhotoShrinkAnimationStart event) {
    currentTransitionState = TransitionState.TO_LISTING;
  }

  @Subscribe
  public void handleOnPhotoShrinkAnimationEnd(OnPhotoShrinkAnimationEnd event) {
    currentTransitionState = TransitionState.LISTING;
  }

  @Subscribe
  public void handleOnPhotoRevealAnimStart(OnPhotoRevealAnimationStart event) {
    currentTransitionState = TransitionState.TO_DETAILS;
  }

  @Subscribe
  public void handleOnPhotoRevealAnimEnd(OnPhotoRevealAnimationEnd event) {
    currentTransitionState = TransitionState.DETAILS;
  }

  public TransitionState getTransitionState() {
    return currentTransitionState;
  }

  public void notifyDataSetChanged() {
    photoListingView.notifyDataSetChanged();
    photoGalleryView.notifyDataSetChanged();
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

  public LightEventBus getEventBus() {
    return eventBus == null ? eventBus = new LightEventBus() : eventBus;
  }

  public DataStore getDataStore() {
    return dataStore == null ? dataStore = new DataStore() : dataStore;
  }

  enum TransitionState {
    LISTING,
    TO_DETAILS,
    DETAILS,
    TO_LISTING
  }

}
