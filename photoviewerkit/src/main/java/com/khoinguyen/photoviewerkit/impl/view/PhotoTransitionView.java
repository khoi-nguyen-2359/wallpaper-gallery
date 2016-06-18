package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.khoinguyen.apptemplate.anim.CompoundViewAnimation;
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.photoviewerkit.impl.customview.ClippingRevealDraweeView;
import com.khoinguyen.apptemplate.eventbus.LightEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.data.ListingItemInfo;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryDragEnd;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoListingItemClick;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRevealAnimationEnd;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRevealAnimationStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRevealAnimationUpdate;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationEnd;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationUpdate;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoShrinkAnimationWillStart;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitComponent;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoTransitionView extends ClippingRevealDraweeView implements IPhotoViewerKitComponent<SharedData> {
  protected CompoundViewAnimation revealAnim;
  protected CompoundViewAnimation shrinkAnim;

  protected IEventBus eventBus;
  protected SharedData sharedData;

  public PhotoTransitionView(Context context, GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
    init();
  }

  public PhotoTransitionView(Context context) {
    super(context);
    init();
  }

  public PhotoTransitionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PhotoTransitionView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public PhotoTransitionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public void init() {
    getHierarchy().setFadeDuration(0);
  }

  @Subscribe
  public void handlePhotoListingItemClick(final OnPhotoListingItemClick event) {
    ListingItemInfo currentSelectedItem = sharedData.getCurrentSelectedItem();

    setVisibility(View.VISIBLE);
    setImageUri(event.getPhotoDisplayInfo().getLowResUri());

    revealAnim = createExpanseAnimation(currentSelectedItem.getItemRect(), event.getFullRect())
        .addAnimatorListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            eventBus.post(new OnPhotoRevealAnimationStart());
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            eventBus.post(new OnPhotoRevealAnimationEnd());
            setVisibility(View.GONE);
          }
        })
        .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            eventBus.post(new OnPhotoRevealAnimationUpdate(animation.getAnimatedFraction()));
          }
        });
    revealAnim.run();
  }

  public void startShrinkAnimation(RectF fullRect) {
    ListingItemInfo currentSelectedItem = sharedData.getCurrentSelectedItem();
    shrinkAnim = createShrinkAnimation(fullRect, currentSelectedItem.getItemRect())
        .addAnimatorListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            setVisibility(View.VISIBLE);
            eventBus.post(new OnPhotoShrinkAnimationStart());
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            setVisibility(View.GONE);
            eventBus.post(new OnPhotoShrinkAnimationEnd());
          }
        })
        .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            eventBus.post(new OnPhotoShrinkAnimationUpdate(animation.getAnimatedFraction()));
          }
        });

    eventBus.post(new OnPhotoShrinkAnimationWillStart());
    shrinkAnim.run();
  }

  @Subscribe
  public void handlePhotoGalleryDraggingEnd(final OnPhotoGalleryDragEnd event) {
    startShrinkAnimation(event.getFullRect());
  }

  @Subscribe
  public void handlePhotoGalleryPageSelected(OnPhotoGalleryPhotoSelect event) {
    //todo: might affect loading speed of gallery itemView
    setImageUri(event.getPhotoDisplayInfo().getLowResUri());
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
}
