package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoOverlayView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 6/22/16.
 */

public class PhotoOverlayView extends FrameLayout implements IPhotoOverlayView<SharedData> {
  private static final long DURATION_FADE_CONTENT = 150;
  private TextView tvPhotoDescription;
  private View mainContainer;
  private PhotoActionView viewPhotoAction;

  private IPhotoViewerKitWidget<SharedData> photoKitWidget;
  private SharedData sharedData;
  private IEventBus eventBus;
  private ObjectAnimator fadeContentAnimator;

  public PhotoOverlayView(Context context) {
    super(context);
    init();
  }

  public PhotoOverlayView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PhotoOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public PhotoOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.photokit_overlay_view, this);
    mainContainer = findViewById(R.id.main_container);
    tvPhotoDescription = (TextView) findViewById(R.id.tv_photo_description);
    tvPhotoDescription.setMovementMethod(LinkMovementMethod.getInstance());
    viewPhotoAction = (PhotoActionView) findViewById(R.id.view_photo_action);
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData>/**/ widget) {
    this.photoKitWidget = widget;
    this.sharedData = widget.getSharedData();
    this.eventBus = widget.getEventBus();
  }

  @Override
  public void setPhotoActionAdapter(IListingAdapter actionAdapter) {
    viewPhotoAction.setActionAdapter(actionAdapter);
  }

  @Override
  public void setPhotoActionEventListener(PhotoActionView.PhotoActionEventListener eventListener) {
    viewPhotoAction.setEventListener(eventListener);
  }

  @Override
  public void bindPhoto(PhotoDisplayInfo photoDisplayInfo) {
    String photoDesc = photoDisplayInfo.getDescription();
    if (TextUtils.isEmpty(photoDesc)) {
      tvPhotoDescription.setText("");
    } else {
      tvPhotoDescription.setText(Html.fromHtml(photoDesc));
    }
    
    viewPhotoAction.bindPhoto(photoDisplayInfo);
  }

  @Override
  public void toggleFading() {
    if (fadeContentAnimator != null && fadeContentAnimator.isRunning()) {
      return;
    }

    if (fadeContentAnimator == null) {
      fadeContentAnimator = ObjectAnimator.ofFloat(this, "alpha", 0);
      fadeContentAnimator.setDuration(DURATION_FADE_CONTENT);
      fadeContentAnimator.setInterpolator(new AccelerateInterpolator());
    }

    final float alpha = getAlpha();
    if (alpha == 1f || alpha == 0f) {
      fadeContentAnimator.setFloatValues(1 - alpha);
    }

    fadeContentAnimator.start();
  }

  @Override
  public void show() {
    setAlpha(1f); // avoid result of fading animator
    setVisibility(VISIBLE);
  }

  @Override
  public void hide() {
    setVisibility(GONE);
  }

  public boolean isVisible() {
    return getVisibility() == VISIBLE;
  }
}
