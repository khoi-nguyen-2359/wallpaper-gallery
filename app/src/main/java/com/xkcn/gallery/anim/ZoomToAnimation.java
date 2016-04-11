package com.xkcn.gallery.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.ViewGroup;

/**
 * Created by khoinguyen on 4/10/16.
 */
public class ZoomToAnimation extends CompoundViewAnimation {
    private RectF startRect;
    private RectF endRect;

    public ZoomToAnimation rects(RectF start, RectF end) {
        startRect = start;
        endRect = end;

        return this;
    }

    @Override
    public void run() {
        addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = target.getLayoutParams();
                lp.width = (int) (startRect.width() * (1 - animation.getAnimatedFraction()) + endRect.width() * animation.getAnimatedFraction());
                lp.height = (int) (startRect.height() * (1 - animation.getAnimatedFraction()) + endRect.height() * animation.getAnimatedFraction());
                target.requestLayout();
            }
        });

        AnimatorSet animatorSet = buildBaseAnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(target, "X", startRect.left, endRect.left),
                ObjectAnimator.ofFloat(target, "Y", startRect.top, endRect.top)
        );

        animatorSet.start();
    }
}
