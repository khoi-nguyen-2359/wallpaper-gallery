package com.khoinguyen.photokit.sample.event;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoRevealAnimationUpdate {
  private float animationProgress;

  public OnPhotoRevealAnimationUpdate(float animationProgress) {
    this.animationProgress = animationProgress;
  }

  public float getAnimationProgress() {
    return animationProgress;
  }
}
