package com.khoinguyen.photoviewerkit.interfaces;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.RectF;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 6/27/16.
 */

public interface IPhotoTransitionView<D> extends IPhotoViewerKitComponent<D> {
	void startRevealAnimation(RectF itemRect, RectF fullRect, AnimatorListenerAdapter animatorListener, ValueAnimator.AnimatorUpdateListener updateListener);

	void startShrinkAnimation(RectF itemRect, RectF fullRect, AnimatorListenerAdapter animatorListener, ValueAnimator.AnimatorUpdateListener updateListener);

	void show();

	void displayPhoto(PhotoDisplayInfo photo);

	void hide();
}
