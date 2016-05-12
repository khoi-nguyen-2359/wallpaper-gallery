package com.khoinguyen.photoviewerkit.view;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.apptemplate.listing.ItemCreator;
import com.khoinguyen.apptemplate.listing.ItemViewHolder;
import com.khoinguyen.apptemplate.listing.adapter.BaseListingViewAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.data.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.data.DataStore;
import com.khoinguyen.photoviewerkit.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photoviewerkit.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.data.PhotoDisplayInfo;
import com.khoinguyen.apptemplate.listing.util.ListingPagerViewAdapter;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoGalleryView extends ViewPager {
  private static final int DEF_OFFSCREEN_PAGE = 1;

  protected L log = L.get("DefaultPhotoGalleryView");
  protected int touchSlop;
  protected float lastInterceptedY;
  protected float lastInterceptedX;
  protected boolean isDragging;
  protected float lastScrollingY;
  protected float lastScrollingX;
  protected BaseListingViewAdapter<PhotoDisplayInfo> photoAdapter;
  protected AdapterPhotoFinder adapterPhotoFinder;

  protected LightEventBus eventBus;

  protected ListingPagerViewAdapter adapterPhotoGallery;

  private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      if (positionOffset == 0) {
        PhotoDisplayInfo photoDisplayInfo = photoAdapter.getData(position);
        updateCurrentSelectedItemInfo(photoDisplayInfo);
        eventBus.post(new OnPhotoGalleryPageSelect(position, photoDisplayInfo));
      }
    }
  };
  private GestureDetectorCompat detector;
  protected DataStore dataStore;


  private void updateCurrentSelectedItemInfo(PhotoDisplayInfo photoDisplayInfo) {
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();
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
    adapterPhotoGallery = new ListingPagerViewAdapter();
    setAdapter(adapterPhotoGallery);

    detector = new GestureDetectorCompat(getContext(), new GestureDetector.OnGestureListener() {
      @Override
      public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override
      public void onShowPress(MotionEvent e) {
      }

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
      }

      @Override
      public void onLongPress(MotionEvent e) {
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        log.d("onFling");
        return false;
      }
    });
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
    detector.onTouchEvent(ev);

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

  public void setListingAdapter(BaseListingViewAdapter<PhotoDisplayInfo> photoAdapter) {
    this.photoAdapter = photoAdapter;
    adapterPhotoGallery.setListingViewAdapter(photoAdapter);
  }

  public void notifyDataSetChanged() {
    adapterPhotoGallery.notifyDataSetChanged();
  }

  public AdapterPhotoFinder getPhotoFinder() {
    if (adapterPhotoFinder == null || adapterPhotoFinder.getAdapter() != photoAdapter) {
      adapterPhotoFinder = new AdapterPhotoFinder(photoAdapter);
    }

    return adapterPhotoFinder;
  }

  public void setEventBus(LightEventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void setDataStore(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  public static class PhotoGalleryItemViewCreator implements ItemCreator {
    private LayoutInflater layoutInflater;

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
    public ItemViewHolder createViewHolder(View view) {
      return new PhotoGalleryItemViewHolder((ZoomableDraweeView) view);
    }
  }

  public static class PhotoGalleryItemViewHolder extends ItemViewHolder<PhotoDisplayInfo> {
    private ZoomableDraweeView itemView;

    private PhotoGalleryItemViewHolder(ZoomableDraweeView itemView) {
      super(itemView);
      this.itemView = itemView;
    }

    @Override
    public void bind(PhotoDisplayInfo data) {
      DraweeController controller = Fresco.newDraweeControllerBuilder()
          .setLowResImageRequest(ImageRequest.fromUri(data.getLowResUri()))
          .setImageRequest(ImageRequest.fromUri(data.getHighResUri()))
          .setOldController(itemView.getController())
          .setCallerContext(this)
          .build();
      itemView.setController(controller);
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
    ListingItemInfo currentSelectedItem = dataStore.getCurrentSelectedItem();
    setCurrentItem(getPhotoFinder().indexOf(currentSelectedItem.getPhotoId()), false);
    setTranslationX(0);
    setTranslationY(0);
    setVisibility(View.VISIBLE);
  }

  /**
   * END - EVENT HANDLERS
   */
}
