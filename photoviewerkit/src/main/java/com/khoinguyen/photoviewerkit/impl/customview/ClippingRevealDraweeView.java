package com.khoinguyen.photoviewerkit.impl.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.transition.Scene;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.apptemplate.anim.CompoundViewAnimation;
import com.khoinguyen.photoviewerkit.impl.anim.ZoomToAnimation;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class ClippingRevealDraweeView extends SimpleDraweeView {
  private static final long DUR_ANIMATION = 190;
  private ScalingUtils.InterpolatingScaleType actualScaleType;

  public ClippingRevealDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
    setupHierarchy(hierarchy);
  }

  public ClippingRevealDraweeView(Context context) {
    super(context);
    setupHierarchy(getHierarchy());
  }

  public ClippingRevealDraweeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupHierarchy(getHierarchy());
  }

  public ClippingRevealDraweeView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setupHierarchy(getHierarchy());
  }

  public ClippingRevealDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setupHierarchy(getHierarchy());
  }

  private void setupHierarchy(GenericDraweeHierarchy hierarchy) {
    if (hierarchy == null) {
      return;
    }

    actualScaleType = new ScalingUtils.InterpolatingScaleType(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER);
    hierarchy.setActualImageScaleType(actualScaleType);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  public void setImageUri(Uri photoUri) {
    DraweeController controller = Fresco.newDraweeControllerBuilder()
        .setCallerContext(null)
        .setUri(photoUri)
        .setOldController(getController())
        .build();
    setController(controller);
  }

  public Rect fromRectF(RectF rectF) {
    return new Rect(0, 0, (int)rectF.width(), (int)rectF.height());
  }

  public CompoundViewAnimation createRevealAnimation(RectF startRect, RectF endRect) {
    actualScaleType = new ScalingUtils.InterpolatingScaleType(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER,
        fromRectF(startRect), fromRectF(endRect));
    getHierarchy().setActualImageScaleType(actualScaleType);
    return new ZoomToAnimation()
        .rects(startRect, endRect)
        .duration(DUR_ANIMATION)
        .target(this)
        .interpolator(new AccelerateInterpolator())
        .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            actualScaleType.setValue(animation.getAnimatedFraction());
          }
        });
  }

  public CompoundViewAnimation createShrinkAnimation(RectF startRect, RectF endRect) {
    actualScaleType = new ScalingUtils.InterpolatingScaleType(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP,
        fromRectF(startRect), fromRectF(endRect));
    getHierarchy().setActualImageScaleType(actualScaleType);
    return new ZoomToAnimation()
        .rects(startRect, endRect)
        .duration(DUR_ANIMATION)
        .target(this)
        .interpolator(new AccelerateInterpolator())
        .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            actualScaleType.setValue(animation.getAnimatedFraction());
          }
        });
  }
}
