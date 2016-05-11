package com.khoinguyen.photokit.sample.view;

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
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.adapter.BaseListingViewAdapter;
import com.khoinguyen.photokit.adapter.ListingViewHolder;
import com.khoinguyen.photokit.adapter.ViewCreator;
import com.khoinguyen.photokit.eventbus.LightEventBus;
import com.khoinguyen.photokit.eventbus.Subscribe;
import com.khoinguyen.photokit.sample.adapter.AdapterPhotoFinder;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.util.ListingPagerViewAdapter;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class DefaultPhotoGalleryView extends ViewPager implements PhotoListingView<BaseListingViewAdapter<PhotoDisplayInfo>> {
  private static final int DEF_OFFSCREEN_PAGE = 1;

  private L log = L.get("DefaultPhotoGalleryView");
  private int touchSlop;
  private float lastInterceptedY;
  private boolean isDragging;
  private float lastScrollingY;
  private float lastScrollingX;
  private BaseListingViewAdapter<PhotoDisplayInfo> photoAdapter;
  private AdapterPhotoFinder adapterPhotoFinder;

  private LightEventBus eventEmitter = LightEventBus.getDefaultInstance();

  private ListingPagerViewAdapter adapterPhotoGallery;

  private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      if (positionOffset == 0) {
        PhotoDisplayInfo photoDisplayInfo = photoAdapter.getData(position);
        updateCurrentSelectedItemInfo(photoDisplayInfo);
        eventEmitter.post(new OnPhotoGalleryPageSelect(position, photoDisplayInfo));
      }
    }
  };
  private GestureDetectorCompat detector;

  private void updateCurrentSelectedItemInfo(PhotoDisplayInfo photoDisplayInfo) {
    currentSelectedItemInfo.setPhotoId(photoDisplayInfo.getPhotoId());
  }

  private DefaultPhotoListingView.PhotoListingItemTrackingInfo currentSelectedItemInfo;

  public DefaultPhotoGalleryView(Context context) {
    super(context);
    init();
  }

  public DefaultPhotoGalleryView(Context context, AttributeSet attrs) {
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
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          return true;
        }

        float yDiff = Math.abs(ev.getRawY() - lastInterceptedY);
        if (yDiff >= touchSlop) {
          log.d("onInterceptTouchEvent CATCHED");
          isDragging = true;
          lastScrollingY = ev.getRawY();
          lastScrollingX = ev.getRawX();

          eventEmitter.post(new OnPhotoGalleryDragStart());

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

        break;
      }

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        if (isDragging) {
          // end of a scroll
          isDragging = false;
          setVisibility(View.GONE);
          eventEmitter.post(new OnPhotoGalleryDragEnd(new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight())));
        }
        break;
      }
    }

    return super.onTouchEvent(ev);
  }

  @Override
  public void setAdapter(BaseListingViewAdapter<PhotoDisplayInfo> photoAdapter) {
    this.photoAdapter = photoAdapter;
    adapterPhotoGallery.setListingViewAdapter(photoAdapter);
  }

  @Override
  public void notifyDataSetChanged() {
    adapterPhotoGallery.notifyDataSetChanged();
  }

  public AdapterPhotoFinder getPhotoFinder() {
    if (adapterPhotoFinder == null || adapterPhotoFinder.getAdapter() != photoAdapter) {
      adapterPhotoFinder = new AdapterPhotoFinder(photoAdapter);
    }

    return adapterPhotoFinder;
  }

  public static class PhotoGalleryItemViewCreator implements ViewCreator {
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
    public ListingViewHolder createViewHolder(View view) {
      return new PhotoGalleryItemViewHolder((ZoomableDraweeView) view);
    }
  }

  public static class PhotoGalleryItemViewHolder extends ListingViewHolder<PhotoDisplayInfo> {
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
    currentSelectedItemInfo = event.getCurrentItemInfo();
  }

  @Subscribe
  public void handlePhotoRevealAnimationEnd(OnPhotoRevealAnimationEnd event) {
    setCurrentItem(getPhotoFinder().indexOf(currentSelectedItemInfo.getPhotoId()), false);
    setTranslationX(0);
    setTranslationY(0);
    setVisibility(View.VISIBLE);
  }

  /**
   * END - EVENT HANDLERS
   */
}
