package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.pageable.IPageableListingView;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.apptemplate.listing.pageable.PageableListingViewCollection;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnShrinkTransitionEnd;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoBackdropView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoListingView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoOverlayView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoTransitionView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoViewerKitWidget extends RelativeLayout implements IPhotoViewerKitWidget<SharedData> {

  private OverlayFadingGestureListener overlayFadingGestureListener;

  @IntDef({TRANS_LISTING, TRANS_TO_GALLERY, TRANS_GALLERY, TRANS_TO_LISTING})
  public @interface TransitionState {}

  public static final int TRANS_LISTING = 0;
  public static final int TRANS_TO_GALLERY = 1;
  public static final int TRANS_GALLERY = 2;
  public static final int TRANS_TO_LISTING = 3;

  protected IPhotoGalleryView<SharedData> photoGalleryView;
  protected IPhotoListingView<SharedData, ? extends IViewHolder> photoListingView;
  protected IPhotoTransitionView<SharedData> transitDraweeView;
  protected IPhotoBackdropView<SharedData> transitBackdrop;
  protected IPhotoOverlayView<SharedData> overlayView;

  protected LightEventBus eventBus;
  protected SharedData sharedData;

  protected PageableListingViewCollection pageableListingViews = new PageableListingViewCollection();
  private IPhotoViewerKitWidget.PagingListener pagingListener;

  private RevealAnimationListener revealAnimationListener = new RevealAnimationListener();

  private GestureDetectorCompat overlayFadingClickDetector;
  private boolean overlayFadingEnabled;

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
    overlayFadingGestureListener = new OverlayFadingGestureListener();
    overlayFadingClickDetector = new GestureDetectorCompat(getContext(), overlayFadingGestureListener);
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

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    overlayFadingClickDetector.onTouchEvent(ev);
    return super.dispatchTouchEvent(ev);
  }

  private void readAttrs(AttributeSet attrs) {
    if (attrs == null) {
      return;
    }
  }

  private void initViews() {
    photoListingView = (IPhotoListingView<SharedData, ? extends IViewHolder>) findViewById(R.id.photokit_photo_listing);
    photoGalleryView = (IPhotoGalleryView<SharedData>) findViewById(R.id.photokit_photo_gallery);
    overlayView = (IPhotoOverlayView<SharedData>) findViewById(R.id.photokit_photo_overlay);

    transitDraweeView = (PhotoTransitionView) findViewById(R.id.photokit_photo_transition);
    transitBackdrop = (PhotoBackdropView) findViewById(R.id.photokit_photo_backdrop);

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
    if (sharedData.getCurrentTransitionState() == TRANS_GALLERY) {
//      photoListingView.toggleActiveItems();
      returnToListing(getWidgetFullRect());
      return true;
    }

    return false;
  }

  private ValueAnimator.AnimatorUpdateListener shrinkAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      transitBackdrop.updateAlphaOnShrinkAnimationUpdate(animation.getAnimatedFraction());
    }
  };

  private AnimatorListenerAdapter shrinkAnimationListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationStart(Animator animation) {
      hideOverlay();
      transitDraweeView.show();
      sharedData.setCurrentTransitionState(TRANS_TO_LISTING);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      transitDraweeView.hide();
      sharedData.setCurrentTransitionState(TRANS_LISTING);
      sharedData.activePhoto(null);
      photoListingView.toggleActiveItems();

      eventBus.post(new OnShrinkTransitionEnd());
    }
  };

  public void setPagingListener(PagingListener pagingListener) {
    this.pagingListener = pagingListener;
  }

  @Override
  public void onPagingNext(IPageableListingView component) {
    if (pagingListener != null) {
      pagingListener.onPagingNext(this);
    }
  }

  private void revealGallery(final ListingItemInfo itemInfo) {
    if (itemInfo == null || !itemInfo.isPhotoValid()) {
      return;
    }

    transitDraweeView.show();
    transitDraweeView.displayPhoto(itemInfo.getPhoto());

    revealAnimationListener.setPhotoId(itemInfo.getPhotoId());
    transitDraweeView.startRevealAnimation(itemInfo.getItemRect(), getWidgetFullRect(), revealAnimationListener, revealAnimUpdateListener);
  }

  @Override
  public void revealGallery(int itemIndex) {
    photoListingView.activatePhotoItem(itemIndex);
    ListingItemInfo currentActiveItem = sharedData.getCurrentActiveItem();
    revealGallery(currentActiveItem);
  }

  @Override
  public void returnToListing(RectF fullRect) {
    photoGalleryView.hide();
    ListingItemInfo currActiveItem = sharedData.getCurrentActiveItem();
    transitDraweeView.startShrinkAnimation(currActiveItem.getItemRect(), fullRect, shrinkAnimationListener, shrinkAnimationUpdateListener);
//    photoListingView.toggleActiveItems();
    photoGalleryView.zoomPrimaryItem(new Matrix());
  }

  private RectF getWidgetFullRect() {
    Rect fullRect = new Rect();
    getDrawingRect(fullRect);
    return new RectF(fullRect);
  }

  private ValueAnimator.AnimatorUpdateListener revealAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      transitBackdrop.updateAlphaOnRevealAnimationUpdate(animation.getAnimatedFraction());
    }
  };

  private class RevealAnimationListener extends AnimatorListenerAdapter {
    private String photoId;

    @Override
    public void onAnimationStart(Animator animation) {
      sharedData.setCurrentTransitionState(TRANS_TO_GALLERY);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      sharedData.setCurrentTransitionState(TRANS_GALLERY);
      photoGalleryView.setCurrentPhoto(photoId);
      photoGalleryView.translate(0, 0);
      photoGalleryView.show();
      transitDraweeView.hide();
      showOverlay();
    }

    public void setPhotoId(String photoId) {
      this.photoId = photoId;
    }
  }

  private void showOverlay() {
    overlayView.show();
    overlayFadingGestureListener.enabled = true;
  }

  private void hideOverlay() {
    overlayView.hide();
    overlayFadingGestureListener.enabled = false;
  }

  @Subscribe
  public void onPhotoGalleryPageSelect(OnPhotoGalleryPhotoSelect event) {
    overlayView.bindPhoto(event.getPhotoDisplayInfo());
  }

  private class OverlayFadingGestureListener extends GestureDetector.SimpleOnGestureListener {
    boolean enabled = false;
    boolean hasLastDownEventEnabled = false;

    @Override
    public boolean onDown(MotionEvent e) {
      return hasLastDownEventEnabled = enabled;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      if (!hasLastDownEventEnabled) {   // prevent a single tap confirmed from a down event while reveal animation not yet finished.
        return false;
      }

      overlayView.toggleFading();
      return true;
    }
  }
}
