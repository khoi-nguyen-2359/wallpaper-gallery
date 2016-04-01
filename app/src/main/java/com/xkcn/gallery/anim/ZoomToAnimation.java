package com.xkcn.gallery.anim;

import android.animation.ObjectAnimator;
import android.graphics.RectF;

/**
 * Created by khoi2359 on 4/1/16.
 */
public class ZoomToAnimation extends CompoundViewAnimation {

    private RectF srcRect;
    private RectF destRect;

    public ZoomToAnimation rects(RectF srcRect, RectF dest) {
        this.srcRect = srcRect;
        destRect = dest;
        return this;
    }

    @Override
    public void run() {
        float scale = 1;
        if (destRect.width() / destRect.height() > srcRect.width() / srcRect.height()) {
            scale = srcRect.height() / destRect.height();
        } else {
            scale = srcRect.width() / destRect.width();
        }

        target.setPivotX(0);
        target.setPivotY(0);

        animatorSet = createAnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", scale, 1f),
                ObjectAnimator.ofFloat(target, "scaleY", scale, 1f),
                ObjectAnimator.ofFloat(target, "x", srcRect.left, destRect.left),
                ObjectAnimator.ofFloat(target, "y", srcRect.top, destRect.top)
        );

        animatorSet.start();
    }
}
