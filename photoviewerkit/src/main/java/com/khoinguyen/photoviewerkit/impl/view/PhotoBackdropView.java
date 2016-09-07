package com.khoinguyen.photoviewerkit.impl.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRecenterAnimationUpdate;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoBackdropView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoBackdropView extends View implements IPhotoBackdropView {
	public static final float TRANSITION_OPAQUE_VALUE = 0.75f;

	public PhotoBackdropView(Context context) {
		super(context);
	}

	public PhotoBackdropView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PhotoBackdropView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PhotoBackdropView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
	}

	@Subscribe
	public void handlePhotoRecenterAnimationUpdate(OnPhotoRecenterAnimationUpdate event) {
		setAlpha(TRANSITION_OPAQUE_VALUE + event.getAnimatedFraction() * (1 - TRANSITION_OPAQUE_VALUE));
	}

	@Subscribe
	public void onPhotoGalleryDragStart(OnPhotoGalleryDragStart event) {
		setAlpha(TRANSITION_OPAQUE_VALUE);
	}

	@Override
	public void attach(IPhotoViewerKitWidget widget) {

	}

	@Override
	public void updateAlphaOnShrinkAnimationUpdate(float animationProgress) {
		setAlpha(TRANSITION_OPAQUE_VALUE * (1 - animationProgress));
	}

	@Override
	public void updateAlphaOnRevealAnimationUpdate(float animationProgress) {
		setAlpha(animationProgress);
	}
}
