package com.khoinguyen.apptemplate.anim;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by khoinguyen on 4/10/16.
 */
public abstract class CompoundViewAnimation {
  protected View target;
  protected Long duration;
  protected TimeInterpolator interpolator;
  protected ValueAnimator updateAnimator;
  protected AnimatorSet currentAnimator = new AnimatorSet();

  public void run() {
    currentAnimator = build();
    currentAnimator.start();
  }

  public boolean isRunning() {
    return currentAnimator.isRunning();
  }

  public boolean isStarted() {
    return currentAnimator.isStarted();
  }

  public void abort() {
    currentAnimator.cancel();
  }

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
    getUpdateAnimator().addListener(listener);
    return this;
  }

  public CompoundViewAnimation addUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
    getUpdateAnimator().addUpdateListener(updateListener);
    return this;
  }

  private ValueAnimator getUpdateAnimator() {
    if (updateAnimator == null) {
      updateAnimator = ValueAnimator.ofFloat(0f, 1f);
    }

    return updateAnimator;
  }

  protected AnimatorSet build() {
    currentAnimator = new AnimatorSet();

    if (target != null) {
      currentAnimator.setTarget(target);
    }

    if (duration != null) {
      currentAnimator.setDuration(duration);
    }

    if (interpolator != null) {
      currentAnimator.setInterpolator(interpolator);
    }

    if (updateAnimator != null) {
      currentAnimator.play(updateAnimator);
    }

    return currentAnimator;
  }
}
