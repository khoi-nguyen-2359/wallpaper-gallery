package com.khoinguyen.photokit.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.khoinguyen.photokit.PhotoBackdropView;
import com.khoinguyen.photokit.PhotoKitWidget;
import com.khoinguyen.photokit.PhotoListingView;
import com.khoinguyen.photokit.PhotoTransitionView;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.eventbus.EventEmitter;
import com.khoinguyen.photokit.sample.binder.DefaultBasePhotoListingViewBinder;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.photokit.sample.event.Subscribe;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class DefaultPhotoKitWidget extends RelativeLayout implements PhotoKitWidget<DefaultBasePhotoListingViewBinder> {
    protected EventEmitter eventEmitter = EventEmitter.getDefaultInstance();

    protected PhotoTransitionView transitDraweeView;
    protected PhotoBackdropView transitBackdrop;
    protected PhotoListingView<DefaultBasePhotoListingViewBinder> photoGalleryView;
    protected PhotoListingView<DefaultBasePhotoListingViewBinder> photoListingView;
    protected TransitionState currentTransitionState = TransitionState.LISTING;

    public DefaultPhotoKitWidget(Context context) {
        super(context);
    }

    public DefaultPhotoKitWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttrs(attrs);
    }

    public DefaultPhotoKitWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultPhotoKitWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttrs(attrs);
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
    public void setBinders(DefaultBasePhotoListingViewBinder listingBinder, DefaultBasePhotoListingViewBinder galleryBinder) {
        photoListingView.setBinder(listingBinder);
        photoGalleryView.setBinder(galleryBinder);
    }

    private void readAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
    }

    private void initViews() {
        photoGalleryView = (PhotoListingView) findViewById(R.id.photokit_pager_photo_gallery);
        transitDraweeView = (PhotoTransitionView) findViewById(R.id.photokit_transition_photo);
        transitBackdrop = (PhotoBackdropView) findViewById(R.id.photokit_transition_backdrop);
        photoListingView = (PhotoListingView) findViewById(R.id.photokit_photo_listing);
    }

    /**
     *
     * @return False means no handling happens
     */
    @Override
    public boolean handleBackPressed() {
        if (currentTransitionState == TransitionState.DETAILS) {
            ((View)photoGalleryView).setVisibility(View.GONE);
            transitDraweeView.startShrinkAnimation(new RectF(0,0,getWidth(),getHeight()));
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

    @Override
    public TransitionState getTransitionState() {
        return currentTransitionState;
    }

    @Override
    public void notifyDataSetChanged() {
        photoListingView.notifyDataSetChanged();
        photoGalleryView.notifyDataSetChanged();
    }

    public void registerEvents() {
        eventEmitter.register(photoGalleryView);
        eventEmitter.register(photoListingView);
        eventEmitter.register(transitDraweeView);
        eventEmitter.register(transitBackdrop);
        eventEmitter.register(this);
    }

    public void unregisterEvents() {
        eventEmitter.unregister(photoGalleryView);
        eventEmitter.unregister(photoListingView);
        eventEmitter.unregister(transitBackdrop);
        eventEmitter.unregister(transitDraweeView);
        eventEmitter.unregister(this);
    }
}
