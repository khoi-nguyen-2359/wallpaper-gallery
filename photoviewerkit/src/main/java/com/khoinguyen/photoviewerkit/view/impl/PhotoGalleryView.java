package com.khoinguyen.photoviewerkit.view.impl;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.AnimatedZoomableController;
import com.facebook.samples.zoomable.DefaultZoomableController;
import com.facebook.samples.zoomable.ZoomableController;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.adapter.ListingAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.data.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.data.SharedData;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photoviewerkit.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;
import com.khoinguyen.apptemplate.listing.util.PagerListingAdapter;
import com.khoinguyen.photoviewerkit.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photoviewerkit.view.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.view.IPhotoViewerKitWidget;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoGalleryView extends ViewPager implements IPhotoGalleryView<SharedData> {
  private static final int DEF_OFFSCREEN_PAGE = 1;

  protected L log = L.get("DefaultPhotoGalleryView");
  protected int touchSlop;
  protected float lastInterceptedY;
  protected float lastInterceptedX;
  protected boolean isDragging;
  protected float lastScrollingY;
  protected float lastScrollingX;
  protected ListingAdapter photoAdapter;
  protected AdapterPhotoFinder adapterPhotoFinder;

  protected IEventBus eventBus;

  protected PhotoGalleryPagerAdapter adapterPhotoGallery;

  private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      if (positionOffset == 0) {
        PhotoDisplayInfo photoDisplayInfo = adapterPhotoFinder.getPhoto(position);
        if (photoDisplayInfo != null) {
          updateCurrentSelectedItemInfo(photoDisplayInfo);
          eventBus.post(new OnPhotoGalleryPageSelect(position, photoDisplayInfo));
        }
      }
    }
  };
  protected SharedData sharedData;


  private void updateCurrentSelectedItemInfo(PhotoDisplayInfo photoDisplayInfo) {
    ListingItemInfo currentSelectedItem = sharedData.getCurrentSelectedItem();
    currentSelectedItem.setPhotoId(photoDisplayInfo.getPhotoId());
  }

  public PhotoGalleryView(Context context) {
    super(context);
    init();
  }

  public PhotoGalleryView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);
    ViewConfiguration vc = ViewConfiguration.get(getContext());
    touchSlop = vc.getScaledTouchSlop();
    isDragging = false;

    addOnPageChangeListener(internalOnPageChangeListener);
    adapterPhotoGallery = new PhotoGalleryPagerAdapter();
    setAdapter(adapterPhotoGallery);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return interceptTouchToDrag(ev) || super.onInterceptTouchEvent(ev);
  }

  private boolean interceptTouchToDrag(MotionEvent ev) {
    log.d("onInterceptTouchEvent TRY");

    int action = ev.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        lastInterceptedY = ev.getRawY();
        lastInterceptedX = ev.getRawX();
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          return true;
        }

        doDraggingDetect(ev);
        if (isDragging) {
          return true;
        }

        break;
      }

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL: {
        isDragging = false;
        break;
      }
    }

    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    int action = ev.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          float yTranslate = ev.getRawY() - lastScrollingY;
          float xTranslate = ev.getRawX() - lastScrollingX;
          setTranslationY(getTranslationY() + yTranslate);
          setTranslationX(getTranslationX() + xTranslate);
          lastScrollingY = ev.getRawY();
          lastScrollingX = ev.getRawX();
          return true;
        }

        doDraggingDetect(ev);
        if (isDragging) {
          return true;
        }

        break;
      }

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        if (isDragging) {
          // end of a scroll
          isDragging = false;
          setVisibility(View.GONE);
          eventBus.post(new OnPhotoGalleryDragEnd(new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight())));
        }
        break;
      }
    }

    return super.onTouchEvent(ev);
  }

  private void doDraggingDetect(MotionEvent ev) {
    float xDiff = Math.abs(ev.getRawX() - lastInterceptedX);
    float yDiff = Math.abs(ev.getRawY() - lastInterceptedY);
    if (yDiff > touchSlop && yDiff * 0.5f > xDiff) {
      log.d("dragging CATCHED");
      isDragging = true;
      lastScrollingY = ev.getRawY();
      lastScrollingX = ev.getRawX();

      eventBus.post(new OnPhotoGalleryDragStart());
    }
  }

  public void setListingAdapter(ListingAdapter photoAdapter) {
    this.photoAdapter = photoAdapter;
    adapterPhotoGallery.setListingViewAdapter(photoAdapter);
    if (adapterPhotoFinder == null || adapterPhotoFinder.getAdapter() != photoAdapter) {
      adapterPhotoFinder = new AdapterPhotoFinder(photoAdapter);
    }
  }

  public void notifyDataSetChanged() {
    adapterPhotoGallery.notifyDataSetChanged();
  }

  public void setEventBus(LightEventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void setSharedData(SharedData sharedData) {
    this.sharedData = sharedData;
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) {
    sharedData = widget.getSharedData();
    eventBus = widget.getEventBus();
  }

  public static class PhotoItemType extends ListingItemType<BaseViewHolder> {
    private LayoutInflater layoutInflater;

    public PhotoItemType(int viewType) {
      super(viewType);
    }

    @Override
    public View createView(ViewGroup container) {
      if (layoutInflater == null) {
        layoutInflater = LayoutInflater.from(container.getContext());
      }

      ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photokit_photo_gallery_pager_item, container, false);
      itemView.getHierarchy().setFadeDuration(0);
      itemView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

      return itemView;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
      return new PhotoGalleryItemViewHolder((ZoomableDraweeView) view);
    }
  }

  public static class PhotoGalleryItemViewHolder extends BaseViewHolder<PhotoDisplayInfo> {
    protected ZoomableDraweeView draweeView;

    private PhotoGalleryItemViewHolder(ZoomableDraweeView itemView) {
      super(itemView);
      this.draweeView = itemView;
      draweeView.setIsLongpressEnabled(false);
    }

    @Override
    public void bind(PhotoDisplayInfo data) {
      DraweeController controller = Fresco.newDraweeControllerBuilder()
          .setLowResImageRequest(ImageRequest.fromUri(data.getLowResUri()))
          .setImageRequest(ImageRequest.fromUri(data.getHighResUri()))
          .setOldController(draweeView.getController())
          .setCallerContext(this)
          .build();
      draweeView.setController(controller);
    }
  }

  /**
   * EVENT HANDLERS
   */

  @Subscribe
  public void handlePhotoListingItemClick(OnPhotoListingItemClick event) {
  }

  @Subscribe
  public void handlePhotoRevealAnimationEnd(OnPhotoRevealAnimationEnd event) {
    ListingItemInfo currentSelectedItem = sharedData.getCurrentSelectedItem();
    setCurrentItem(adapterPhotoFinder.indexOf(currentSelectedItem.getPhotoId()), false);
    setTranslationX(0);
    setTranslationY(0);
    setVisibility(View.VISIBLE);
  }

  @Subscribe
  public void handlePhotoShrinkAnimWillStart(OnPhotoShrinkAnimationWillStart event) {
    zoomPrimaryItemToOriginal();
  }

  private void zoomPrimaryItemToOriginal() {
    ZoomableDraweeView primaryItemView = (ZoomableDraweeView) findViewWithTag(adapterPhotoGallery.primaryItemAdapterPosition);
    if (primaryItemView == null) {
      return;
    }

    ZoomableController zoomableController = primaryItemView.getZoomableController();
    if (zoomableController instanceof DefaultZoomableController) {
      DefaultZoomableController animatedZoomableController = (DefaultZoomableController) zoomableController;
      animatedZoomableController.setTransform(new Matrix());
    }
  }

  /**
   * END - EVENT HANDLERS
   */

  static class PhotoGalleryPagerAdapter extends PagerListingAdapter {
    int primaryItemAdapterPosition;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
      super.setPrimaryItem(container, position, object);

      if (object instanceof View) {
        View itemView = (View) object;
        itemView.setTag(position);
        primaryItemAdapterPosition = position;
      }
    }
  }
}
