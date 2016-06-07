package com.khoinguyen.photoviewerkit.zoomable;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.facebook.samples.gestures.TransformGestureDetector;
import com.facebook.samples.zoomable.AnimatedZoomableController;
import com.facebook.samples.zoomable.DefaultZoomableController;

/**
 * Created by khoinguyen on 6/7/16.
 */

public class DoubleTapZoomableController extends AnimatedZoomableController {
  private final GestureDetector.OnGestureListener doubleTapListener = new GestureDetector.SimpleOnGestureListener() {
    @Override
    public boolean onDoubleTap(MotionEvent e) {
      if (isIdentity()) {
        RectF imageBounds = getImageBounds();
        PointF imagePoint = new PointF(e.getX() / imageBounds.width(), e.getY() / imageBounds.height());
        zoomToPoint(2.0f, imagePoint, new PointF(e.getX(), e.getY()), DefaultZoomableController.LIMIT_ALL, 500, null);
      } else {
        setTransform(new Matrix(), 500, null);
      }

      return true;
    }
  };

  private GestureDetector doubleTapDetector;

  public DoubleTapZoomableController(Context context, TransformGestureDetector transformGestureDetector) {
    super(transformGestureDetector);

    doubleTapDetector = new GestureDetector(context, doubleTapListener);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isEnabled()) {
      if (doubleTapDetector.onTouchEvent(event)) {
        return true;
      }
    }

    return super.onTouchEvent(event);
  }

  public static DoubleTapZoomableController newInstance(Context context) {
    return new DoubleTapZoomableController(context, TransformGestureDetector.newInstance());
  }
}
