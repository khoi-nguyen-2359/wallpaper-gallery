package com.khoinguyen.photokit.sample.view;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
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
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.photokit.ItemViewHolder;
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.binder.PhotoListingViewBinder;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.eventbus.EventEmitter;
import com.khoinguyen.photokit.sample.binder.DefaultBasePhotoListingViewBinder;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photokit.sample.event.Subscribe;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.sample.model.PhotoListingItemTrackingInfo;
import com.khoinguyen.util.log.L;

import java.util.Map;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class DefaultPhotoGalleryView extends ViewPager implements PhotoListingView {
    private static final int DEF_OFFSCREEN_PAGE = 1;

    private L log;
    private int touchSlop;
    private float lastInterceptedY;
    private boolean isDragging;
    private float lastScrollingY;
    private float lastScrollingX;
    private Binder binder;

    private EventEmitter eventEmitter = EventEmitter.getDefaultInstance();

    private PhotoGalleryPagerAdapter adapterPhotoGallery;

    private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0) {
                PhotoDisplayInfo photoDisplayInfo = binder.getPhotoDisplayInfo(position);
                updateCurrentSelectedItemInfo(position, photoDisplayInfo);
                eventEmitter.post(new OnPhotoGalleryPageSelect(position, photoDisplayInfo));
            }
        }
    };

    private void updateCurrentSelectedItemInfo(int position, PhotoDisplayInfo photoDisplayInfo) {
        currentSelectedItemInfo.setItemIndex(position);
        currentSelectedItemInfo.setItemPhoto(photoDisplayInfo);
    }

    private PhotoListingItemTrackingInfo currentSelectedItemInfo;

    public DefaultPhotoGalleryView(Context context) {
        super(context);
        init();
    }

    public DefaultPhotoGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        log = L.get(this);
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

                    eventEmitter.post(new OnPhotoGalleryDragStart(getCurrentItem()));

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

        return super.onInterceptTouchEvent(ev);
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

    public void setBinder(PhotoListingViewBinder binder) {
        this.binder = (Binder) binder;
    }

    @Override
    public void notifyDataSetChanged() {
        adapterPhotoGallery.notifyDataSetChanged();
    }

    /**
     * Created by khoinguyen on 12/14/15.
     */
    public class PhotoGalleryPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = binder.getItemView(container, position);
            binder.bindItemData(itemView, position);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                binder.destroyViewHolders((View) object);
                container.removeView((View) object);
            }
        }

        @Override
        public int getCount() {
            return binder == null ? 0 : binder.getItemCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * Created by khoinguyen on 5/2/16.
     */
    public abstract static class Binder extends DefaultBasePhotoListingViewBinder {
        private LayoutInflater layoutInflater;

        @Override
        public View getItemView(ViewGroup container, int itemIndex) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(container.getContext());
            }

            ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photokit_photo_gallery_pager_item, container, false);
            itemView.getHierarchy().setFadeDuration(0);
            itemView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

            return itemView;
        }

        @Override
        public ItemViewHolder<PhotoDisplayInfo> createPhotoDisplayItemViewHolder(View itemView) {
            return new PhotoGalleryItemViewHolder((ZoomableDraweeView) itemView);
        }

        public void destroyViewHolders(View itemView) {
            mapViewHolder.remove(itemView);
            mapDefaultViewHolder.remove(itemView);
        }
    }

    public static class PhotoGalleryItemViewHolder extends ItemViewHolder<PhotoDisplayInfo> {
        private ZoomableDraweeView itemView;

        private PhotoGalleryItemViewHolder(ZoomableDraweeView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        @Override
        public void bindItemData(int itemIndex, PhotoDisplayInfo data) {
            super.bindItemData(itemIndex, data);

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
        setCurrentItem(event.getItemPosition(), false);
        setTranslationX(0);
        setTranslationY(0);
        setVisibility(View.VISIBLE);
    }

    /**
     * END - EVENT HANDLERS
     */
}
