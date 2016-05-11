package com.khoinguyen.photokit.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.khoinguyen.photokit.PhotoBackdropView;
import com.khoinguyen.photokit.eventbus.Subscribe;
import com.khoinguyen.photokit.sample.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photokit.sample.event.OnPhotoRevealAnimationUpdate;
import com.khoinguyen.photokit.sample.event.OnPhotoShrinkAnimationUpdate;

/**
 * Created by khoinguyen on 4/25/16.
 */
public class DefaultPhotoBackdropView extends View implements PhotoBackdropView {
  public DefaultPhotoBackdropView(Context context) {
    super(context);
  }

  public DefaultPhotoBackdropView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DefaultPhotoBackdropView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DefaultPhotoBackdropView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawColor(Color.BLACK);
  }

  @Subscribe
  public void handlePhotoRevealAnimationUpdate(OnPhotoRevealAnimationUpdate event) {
    setAlpha(event.getAnimationProgress());
  }

  @Subscribe
  public void handlePhotoShrinkAnimationUpdate(OnPhotoShrinkAnimationUpdate event) {
    setAlpha(0.75f * (1 - event.getAnimationProgress()));
  }

  @Subscribe
  public void handlePhotoGalleryDragStart(OnPhotoGalleryDragStart event) {
    setAlpha(0.75f);
  }
}
