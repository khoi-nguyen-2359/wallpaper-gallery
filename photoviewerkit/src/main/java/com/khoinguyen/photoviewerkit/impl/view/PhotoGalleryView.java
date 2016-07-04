package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.DefaultZoomableController;
import com.facebook.samples.zoomable.ZoomableController;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.anim.ZoomToAnimation;
import com.khoinguyen.photoviewerkit.impl.util.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRecenterAnimationUpdate;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.apptemplate.listing.adapter.PagerListingAdapter;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoGalleryView extends ViewPager implements IPhotoGalleryView<SharedData> {
  private static final int PAGING_OFFSET = 3;
  private static final int DEF_OFFSCREEN_PAGE = 1;
  private static final int END_DRAG_MIN_DISTANCE_DPS = 75;
  private static final long DURATION_DRAG_CANCEL = 200;

  protected L log = L.get("DefaultPhotoGalleryView");
  protected int touchSlop;
  protected float endDragMinDistance;
  protected float lastInterceptY;
  protected float lastInterceptX;
  protected boolean isDragging;
  protected float lastDraggingY;
  protected float lastDraggingX;
  protected AdapterPhotoFinder adapterPhotoFinder;

  protected IEventBus eventBus;

  protected PhotoGalleryPagerAdapter adapterPhotoGallery;

  protected IPhotoViewerKitWidget<SharedData> photoKitWidget;

  protected SharedData sharedData;

  private boolean pagingNextHasFired = false;

  private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      if (positionOffset == 0) {
        onPagerPageSelected(position);
      } else {
        onPagerPageScrolled(position, positionOffset, positionOffsetPixels);
      }
    }
  };

  private void onPagerPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  private void onPagerPageSelected(int position) {
    updateCurrentSelectedItemInfo(position);
    checkPagingNext(position);
  }

  private void checkPagingNext(int position) {
    if (!pagingNextHasFired && position >= adapterPhotoGallery.getCount() - PAGING_OFFSET) {
      photoKitWidget.onPagingNext(this);
      pagingNextHasFired = true;
    }
  }

  private void updateCurrentSelectedItemInfo(int position) {
    PhotoDisplayInfo photoDisplayInfo = adapterPhotoFinder.getPhoto(position);
    if (photoDisplayInfo == null) {
      return;
    }

    ListingItemInfo currentSelectedItem = sharedData.getCurrentActiveItem();
    currentSelectedItem.setPhoto(photoDisplayInfo);

    eventBus.post(new OnPhotoGalleryPhotoSelect(position, photoDisplayInfo));
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

    DisplayMetrics dm = getResources().getDisplayMetrics();
    endDragMinDistance = dm.density * END_DRAG_MIN_DISTANCE_DPS;

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
        lastInterceptX = ev.getRawX();
        lastInterceptY = ev.getRawY();
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          return true;
        }

        detectDrag(ev.getRawX(), ev.getRawY());
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
          dragTo(ev.getRawX(), ev.getRawY());
          return true;
        }

        detectDrag(ev.getRawX(), ev.getRawY());
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

          onDragEnd(ev.getRawX(), ev.getRawY());
        }
        break;
      }
    }

    return super.onTouchEvent(ev);
  }

  private void onDragEnd(float endX, float endY) {
    double dragDistance = Math.hypot(endX - lastInterceptX, endY - lastInterceptY);
    if (dragDistance > endDragMinDistance) {
      RectF fullRect = getCurrentRect();
      photoKitWidget.returnToListing(fullRect);
    } else {
      new ZoomToAnimation()
          .rects(getCurrentRect(), new RectF(0,0,getWidth(),getHeight()))
          .duration(DURATION_DRAG_CANCEL)
          .target(this)
          .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              eventBus.post(new OnPhotoRecenterAnimationUpdate(animation.getAnimatedFraction()));
            }
          })
          .run();
    }
  }

  private RectF getCurrentRect() {
    return new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight());
  }

  /**
   * Drag this view to new position by translating its x and y
   * @param draggingX new x
   * @param draggingY new y
   */
  private void dragTo(float draggingX, float draggingY) {
    float xTranslate = draggingX - lastDraggingX;
    float yTranslate = draggingY - lastDraggingY;
    translate(getTranslationX() + xTranslate, getTranslationY() + yTranslate);
    lastDraggingX = draggingX;
    lastDraggingY = draggingY;
  }

  @Override
  public void translate(float x, float y) {
    setTranslationX(x);
    setTranslationY(y);
  }

  /**
   * Check if these new values of x,y will start a drag on this view
   * @param draggingX new x
   * @param draggingY new y
   * @return true if a drag has started, otherwise false
   */
  private boolean detectDrag(float draggingX, float draggingY) {
    if (isDragging) {
      return true;
    }

    float xDiff = Math.abs(draggingX - lastInterceptX);
    float yDiff = Math.abs(draggingY - lastInterceptY);
    if (yDiff > touchSlop && yDiff * 0.5f > xDiff) {
      log.d("dragging CATCHED");
      isDragging = true;
      lastDraggingX = draggingX;
      lastDraggingY = draggingY;

      eventBus.post(new OnPhotoGalleryDragStart());

      return true;
    }

    return false;
  }

  public void setPhotoAdapter(IListingAdapter photoAdapter) {
    adapterPhotoGallery.setListingViewAdapter(photoAdapter);
    if (adapterPhotoFinder == null || adapterPhotoFinder.getAdapter() != photoAdapter) {
      adapterPhotoFinder = new AdapterPhotoFinder(photoAdapter);
    }
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) throws UnsupportedOperationException {
    sharedData = widget.getSharedData();
    eventBus = widget.getEventBus();
    photoKitWidget = widget;
  }

  @Override
  public void enablePaging() {
    pagingNextHasFired = false;
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

  @Override
  public void setCurrentPhoto(String photoId) {
    int itemIndex = adapterPhotoFinder.indexOf(photoId);
    if (itemIndex == AdapterPhotoFinder.NO_POSITION) {
      return;
    }

    setCurrentItem(itemIndex, false);
  }

  @Override
  public void setCurrentPhoto(int itemIndex) {
    PhotoDisplayInfo photo = adapterPhotoFinder.getPhoto(itemIndex);
    if (photo == null) {
      return;
    }

    setCurrentItem(itemIndex, false);
  }

  @Override
  public void show() {
    setVisibility(VISIBLE);
  }

  @Override
  public void zoomPrimaryItem(Matrix transformMatrix) {
    ZoomableDraweeView primaryItemView = (ZoomableDraweeView) findViewWithTag(adapterPhotoGallery.primaryItemAdapterPosition);
    if (primaryItemView == null) {
      return;
    }

    ZoomableController zoomableController = primaryItemView.getZoomableController();
    if (zoomableController instanceof DefaultZoomableController) {
      DefaultZoomableController animatedZoomableController = (DefaultZoomableController) zoomableController;
      animatedZoomableController.setTransform(transformMatrix);
    }
  }

  @Override
  public void hide() {
    setVisibility(GONE);
  }

  /**
   * END - EVENT HANDLERS
   */

  private static class PhotoGalleryPagerAdapter extends PagerListingAdapter {
    int primaryItemAdapterPosition;

    @Override
    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

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
