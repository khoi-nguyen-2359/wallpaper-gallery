package com.xkcn.gallery.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.khoinguyen.logging.L;
import com.xkcn.gallery.adapter.PhotoDetailsPagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;

import java.util.List;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoDetailsViewPager extends ViewPager implements PhotoListingView {
    private static final int DEF_OFFSCREEN_PAGE = 1;
    private PhotoDetailsPagerAdapter adapterPhotoDetails;
    private PhotoListingViewPresenter presenter;
    private L log;
    private int touchSlop;
    private float lastInterceptedY;
    private boolean isDragging;
    private float lastScrollingY;
    private float lastScrollingX;
    private DraggingListener draggingListener;

    public PhotoDetailsViewPager(Context context) {
        super(context);
        init();
    }

    public PhotoDetailsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        log = L.get(this);
        setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        touchSlop = vc.getScaledTouchSlop();
        isDragging = false;
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
                    if (draggingListener != null) {
                        draggingListener.onStartDragging(this);
                    }

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
                    if (draggingListener != null) {
                        draggingListener.onEndDragging(this);
                    }
                }
                break;
            }
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void populatePhotoData(List<PhotoDetails> photos) {
        if (adapterPhotoDetails == null) {
            adapterPhotoDetails = new PhotoDetailsPagerAdapter();
            setAdapter(adapterPhotoDetails);
        }

        adapterPhotoDetails.setPhotoDatas(photos);
        adapterPhotoDetails.notifyDataSetChanged();
    }

    @Override
    public void displayPhotoItem(int position) {
        setCurrentItem(position);
    }

    @Override
    public View getPhotoItemView(int position) {
        throw new UnsupportedOperationException();
    }

    public void setPresenter(PhotoListingViewPresenter presenter) {
        this.presenter = presenter;
    }

    public void setDraggingListener(DraggingListener draggingListener) {
        this.draggingListener = draggingListener;
    }

    public interface DraggingListener {
        void onStartDragging(PhotoDetailsViewPager detailsPager);
        void onEndDragging(PhotoDetailsViewPager detailsPager);
    }
}
