package com.khoinguyen.photokit.sample.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.khoinguyen.photokit.anim.CompoundViewAnimation;
import com.khoinguyen.photokit.customview.ClippingRevealDraweeView;
import com.khoinguyen.photokit.eventbus.LightEventBus;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryPageSelect;
import com.khoinguyen.photokit.sample.event.OnPhotoListingItemClick;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationUpdate;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationUpdate;
import com.khoinguyen.photokit.PhotoTransitionView;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photokit.sample.event.Subscribe;
import com.khoinguyen.photokit.sample.model.PhotoListingItemTrackingInfo;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class DefaultPhotoTransitionView extends ClippingRevealDraweeView implements PhotoTransitionView {
    private CompoundViewAnimation revealAnim;
    private CompoundViewAnimation shrinkAnim;

    private PhotoListingItemTrackingInfo currentSelectedItemInfo;

    private LightEventBus eventEmitter = LightEventBus.getDefaultInstance();

    public DefaultPhotoTransitionView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public DefaultPhotoTransitionView(Context context) {
        super(context);
        init();
    }

    public DefaultPhotoTransitionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultPhotoTransitionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DefaultPhotoTransitionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        getHierarchy().setFadeDuration(0);
    }

    @Subscribe
    public void handlePhotoListingItemClick(final OnPhotoListingItemClick event) {
        currentSelectedItemInfo = event.getCurrentItemInfo();

        setVisibility(View.VISIBLE);
        setImageUri(currentSelectedItemInfo.getItemPhoto().getLowResUri());

        revealAnim = createExpanseAnimation(currentSelectedItemInfo.getItemRect(), event.getFullRect())
                .addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        eventEmitter.post(new OnPhotoRevealAnimationStart());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        eventEmitter.post(new OnPhotoRevealAnimationEnd(currentSelectedItemInfo.getItemIndex()));
                        setVisibility(View.GONE);
                    }
                })
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        eventEmitter.post(new OnPhotoRevealAnimationUpdate(animation.getAnimatedFraction()));
                    }
                });
        revealAnim.run();
    }

    @Override
    public void startShrinkAnimation(RectF fullRect) {
        shrinkAnim = createShrinkAnimation(fullRect, currentSelectedItemInfo.getItemRect())
                .addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setVisibility(View.VISIBLE);
                        eventEmitter.post(new OnPhotoShrinkAnimationStart());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.GONE);
                        eventEmitter.post(new OnPhotoShrinkAnimationEnd());
                    }
                })
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        eventEmitter.post(new OnPhotoShrinkAnimationUpdate(animation.getAnimatedFraction()));
                    }
                });

        eventEmitter.post(new OnPhotoShrinkAnimationWillStart());
        shrinkAnim.run();
    }

    @Subscribe
    public void handlePhotoGalleryDraggingEnd(final OnPhotoGalleryDragEnd event) {
        startShrinkAnimation(event.getFullRect());
    }

    @Subscribe
    public void handlePhotoGalleryPageSelected(OnPhotoGalleryPageSelect event) {
        //todo: might affect loading speed of gallery itemView
        setImageUri(event.getPhotoDisplayInfo().getLowResUri());
    }
}
