package com.khoinguyen.photoviewerkit.impl.event;

/**
 * Created by khoinguyen on 6/13/16.
 */
public class OnPhotoRecenterAnimationUpdate {
  private float animatedFraction;

  public OnPhotoRecenterAnimationUpdate(float animatedFraction) {
    this.animatedFraction = animatedFraction;
  }

  public float getAnimatedFraction() {
    return animatedFraction;
  }
}
