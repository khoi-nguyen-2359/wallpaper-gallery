package com.khoinguyen.photoviewerkit.impl.zoomable;

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
	private static long DURATION_ZOOM = 200;

	private final GestureDetector.OnGestureListener doubleTapListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isIdentity()) {
				RectF imageBounds = getImageBounds();
				PointF imagePoint = new PointF(e.getX() / imageBounds.width(), e.getY() / imageBounds.height());
				zoomToPoint(2.0f, imagePoint, new PointF(e.getX(), e.getY()), DefaultZoomableController.LIMIT_ALL, DURATION_ZOOM, null);
			} else {
				setTransform(new Matrix(), DURATION_ZOOM, null);
			}

			return true;
		}
	};

	private GestureDetector doubleTapDetector;

	public DoubleTapZoomableController(Context context, TransformGestureDetector transformGestureDetector) {
		super(transformGestureDetector);

		doubleTapDetector = new GestureDetector(context, doubleTapListener);
	}

	public static DoubleTapZoomableController newInstance(Context context) {
		return new DoubleTapZoomableController(context, TransformGestureDetector.newInstance());
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
}
