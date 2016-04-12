package com.xkcn.gallery.view.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.khoinguyen.logging.L;
import com.xkcn.gallery.anim.CompoundViewAnimation;
import com.xkcn.gallery.anim.ZoomToAnimation;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class ClippingRevealDraweeView extends SimpleDraweeView {
    private ScalingUtils.InterpolatingScaleType revealScaleType;

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

        revealScaleType = new ScalingUtils.InterpolatingScaleType(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER);
        hierarchy.setActualImageScaleType(revealScaleType);
    }

    public void setImageUris(Uri photoUri) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setCallerContext(null)
                .setUri(photoUri)
                .setOldController(getController())
                .build();
        setController(controller);
    }

    public CompoundViewAnimation createRevealAnimation(final View backdrop, RectF startRect, RectF endRect) {
        return new ZoomToAnimation()
                .rects(startRect, endRect)
                .duration(300)
                .target(this)
                .interpolator(new DecelerateInterpolator())
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        revealScaleType.setValue(animation.getAnimatedFraction());
                        // this animation includes requestLayout() calls, no need invalidate()
                        
                        if (backdrop != null) {
                            backdrop.setAlpha(animation.getAnimatedFraction());
                        }
                    }
                });
    }
}
