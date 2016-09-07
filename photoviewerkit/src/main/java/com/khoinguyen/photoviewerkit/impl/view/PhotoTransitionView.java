package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.khoinguyen.apptemplate.anim.CompoundViewAnimation;
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.photoviewerkit.impl.customview.ClippingRevealDraweeView;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoTransitionView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class PhotoTransitionView extends ClippingRevealDraweeView implements IPhotoTransitionView<SharedData> {
	protected CompoundViewAnimation revealAnim;
	protected CompoundViewAnimation shrinkAnim;

	protected IEventBus eventBus;
	protected SharedData sharedData;

	public PhotoTransitionView(Context context, GenericDraweeHierarchy hierarchy) {
		super(context, hierarchy);
		init();
	}

	public PhotoTransitionView(Context context) {
		super(context);
		init();
	}

	public PhotoTransitionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhotoTransitionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PhotoTransitionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public void init() {
		getHierarchy().setFadeDuration(0);
	}

	@Override
	public void startRevealAnimation(RectF itemRect, RectF fullRect, AnimatorListenerAdapter animatorListener, ValueAnimator.AnimatorUpdateListener updateListener) {
		revealAnim = createRevealAnimation(itemRect, fullRect)
			.addAnimatorListener(animatorListener)
			.addUpdateListener(updateListener);
		revealAnim.run();
	}

	@Override
	public void startShrinkAnimation(RectF itemRect, RectF fullRect, AnimatorListenerAdapter animatorListener, ValueAnimator.AnimatorUpdateListener updateListener) {
		shrinkAnim = createShrinkAnimation(fullRect, itemRect)
			.addAnimatorListener(animatorListener)
			.addUpdateListener(updateListener);
		shrinkAnim.run();
	}

	@Subscribe
	public void handlePhotoGalleryPageSelected(OnPhotoGalleryPhotoSelect event) {
		//todo: might affect loading speed of gallery itemView
		displayPhoto(event.getPhotoDisplayInfo());
	}

	@Override
	public void attach(IPhotoViewerKitWidget<SharedData> widget) {
		sharedData = widget.getSharedData();
		eventBus = widget.getEventBus();
	}

	@Override
	public void show() {
		setVisibility(View.VISIBLE);
	}

	@Override
	public void displayPhoto(PhotoDisplayInfo photo) {
		setImageUri(photo.getLowResUri());
	}

	@Override
	public void hide() {
		setVisibility(GONE);
	}
}
