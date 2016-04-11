package com.xkcn.gallery.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 4/10/16.
 */
public abstract class CompoundViewAnimation {
    protected View target;
    protected ValueAnimator updateAnimator;
    protected List<AnimatorListenerAdapter> animatorListeners;
    protected Long duration;
    protected TimeInterpolator interpolator;

    public abstract void run();

    public CompoundViewAnimation target(View target) {
        this.target = target;
        return this;
    }

    public CompoundViewAnimation duration(long duration) {
        this.duration = duration;
        return this;
    }

    public CompoundViewAnimation interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public CompoundViewAnimation addAnimatorListener(AnimatorListenerAdapter listener) {
        if (animatorListeners == null) {
            animatorListeners = new ArrayList<>();
        }

        animatorListeners.add(listener);
        return this;
    }

    public CompoundViewAnimation addUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        if (updateAnimator == null) {
            updateAnimator = ValueAnimator.ofFloat(0f, 1f);
        }

        updateAnimator.addUpdateListener(updateListener);
        return this;
    }

    protected AnimatorSet buildBaseAnimatorSet() {
        AnimatorSet coreAnimator = new AnimatorSet();

        if (target != null) {
            coreAnimator.setTarget(target);
        }

        if (duration != null) {
            coreAnimator.setDuration(duration);
        }

        if (interpolator != null) {
            coreAnimator.setInterpolator(interpolator);
        }

        if (updateAnimator != null) {
            coreAnimator.play(updateAnimator);
        }

        if (animatorListeners != null) {
            for (AnimatorListenerAdapter listener : animatorListeners) {
                coreAnimator.addListener(listener);
            }
        }

        return coreAnimator;
    }
}
