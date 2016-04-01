package com.xkcn.gallery.anim;

import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by khoi2359 on 4/1/16.
 */
public abstract class CompoundViewAnimation {
    static final int DURATION_UNSET = -1;

    AnimatorSet animatorSet;
    View target;
    int duration = DURATION_UNSET;
    Interpolator interpolator = null;

    public abstract void run();

    public CompoundViewAnimation target(View target) {
        this.target = target;
        return this;
    }

    public CompoundViewAnimation duration(int duration) {
        this.duration = duration;
        return this;
    }

    public CompoundViewAnimation interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    AnimatorSet createAnimatorSet() {
        AnimatorSet animator = new AnimatorSet();
        if (duration != DURATION_UNSET) {
            animator.setDuration(duration);
        }

        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }

        return animator;
    }
}
