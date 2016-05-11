package com.khoinguyen.photokit.sample.event;

/**
 * Created by khoinguyen on 4/26/16.
 */
public class OnPhotoShrinkAnimationUpdate {
  private float animationProgress;

  public OnPhotoShrinkAnimationUpdate(float animationProgress) {
    this.animationProgress = animationProgress;
  }

  public float getAnimationProgress() {
    return animationProgress;
  }
}
