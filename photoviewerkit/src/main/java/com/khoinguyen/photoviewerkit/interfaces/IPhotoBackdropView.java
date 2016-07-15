package com.khoinguyen.photoviewerkit.interfaces;

/**
 * Created by khoinguyen on 6/27/16.
 */
public interface IPhotoBackdropView<D> extends IPhotoViewerKitComponent<D> {
  void updateAlphaOnShrinkAnimationUpdate(float animationProgress);

  void updateAlphaOnRevealAnimationUpdate(float animationProgress);
}
