package com.khoinguyen.photoviewerkit.view.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.data.SharedData;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.photoviewerkit.view.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.view.IPhotoListingView;
import com.khoinguyen.photoviewerkit.view.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoViewerKitWidget extends RelativeLayout implements IPhotoViewerKitWidget<SharedData> {
  protected LightEventBus eventBus;

  protected IPhotoGalleryView<SharedData> photoGalleryView;
  protected IPhotoListingView<SharedData> photoListingView;

  protected PhotoTransitionView transitDraweeView;
  protected PhotoBackdropView transitBackdrop;

  protected TransitState currentTransitState = TransitState.LISTING;
  protected SharedData sharedData;

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
    photoListingView = (IPhotoListingView<SharedData>) findViewById(R.id.photokit_photo_listing);
    photoGalleryView = (IPhotoGalleryView<SharedData>) findViewById(R.id.photokit_pager_photo_gallery);

    transitDraweeView = (PhotoTransitionView) findViewById(R.id.photokit_transition_photo);
    transitBackdrop = (PhotoBackdropView) findViewById(R.id.photokit_transition_backdrop);

    photoListingView.attach(this);
    photoGalleryView.attach(this);
    transitDraweeView.attach(this);
  }

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

  public LightEventBus getEventBus() {
    return eventBus == null ? eventBus = new LightEventBus() : eventBus;
  }

  public SharedData getSharedData() {
    return sharedData == null ? sharedData = new SharedData() : sharedData;
  }

  @Override
  public void openGalleryView(String photoId) {

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

  enum TransitState {
    LISTING,
    TO_GALLERY,
    GALLERY,
    TO_LISTING
  }

}
