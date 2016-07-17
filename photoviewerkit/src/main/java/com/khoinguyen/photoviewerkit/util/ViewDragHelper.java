package com.khoinguyen.photoviewerkit.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by khoinguyen on 7/14/16.
 */
public class ViewDragHelper {
  protected int touchSlop;
  protected float lastInterceptY = -1;
  protected float lastInterceptX = -1;
  protected boolean isDragging;
  protected float lastDraggingY;
  protected float lastDraggingX;
  private DragEventListener dragEventListener;

  public ViewDragHelper(Context context) {
    ViewConfiguration vc = ViewConfiguration.get(context);
    touchSlop = vc.getScaledTouchSlop();

    reset();
  }

  public void reset() {
    lastInterceptX = lastInterceptY = -1;
    isDragging = false;
  }

  public boolean onInterceptTouchEvent(MotionEvent ev) {
    int action = ev.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        lastInterceptX = ev.getRawX();
        lastInterceptY = ev.getRawY();
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          return true;
        }

        detectDrag(ev.getRawX(), ev.getRawY());
        if (isDragging) {
          return true;
        }

        break;
      }

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL: {
        isDragging = false;
        break;
      }
    }

    return false;
  }

  /**
   * Check if these new values of x,y will start a drag on this view
   *
   * @param draggingX new x
   * @param draggingY new y
   * @return true if a drag has started, otherwise false
   */
  private boolean detectDrag(float draggingX, float draggingY) {
    if (lastInterceptX == -1 || lastInterceptY == -1) {
      return false;
    }

    if (isDragging) {
      return true;
    }

    float xDiff = Math.abs(draggingX - lastInterceptX);
    float yDiff = Math.abs(draggingY - lastInterceptY);
    if (yDiff > touchSlop && yDiff * 0.5f > xDiff) {
      isDragging = true;
      lastDraggingX = draggingX;
      lastDraggingY = draggingY;

      if (dragEventListener != null) {
        dragEventListener.onDragStart();
      }

      return true;
    }

    return false;
  }

  public boolean onTouchEvent(MotionEvent ev) {
//    log.d("onTouchEvent");

    int action = ev.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_MOVE: {
        if (isDragging) {
          dragTo(ev.getRawX(), ev.getRawY());
          return true;
        }

        detectDrag(ev.getRawX(), ev.getRawY());
        if (isDragging) {
          return true;
        }

        break;
      }

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        if (isDragging) {
          // end of a scroll
          isDragging = false;

          onDragEnd(ev.getRawX(), ev.getRawY());
        }
        break;
      }
    }

    return false;
  }

  private void onDragEnd(float endX, float endY) {
    if (dragEventListener != null) {
      dragEventListener.onDragEnd(endX - lastInterceptX, endY - lastInterceptY);
    }

    reset();
  }

  /**
   * Drag this view to new position by translating its x and y
   *
   * @param draggingX new x
   * @param draggingY new y
   */
  private void dragTo(float draggingX, float draggingY) {
    float currTranslateX = draggingX - lastDraggingX;
    float currTranslateY = draggingY - lastDraggingY;
    if (dragEventListener != null) {
      dragEventListener.onDragUpdate(currTranslateX, currTranslateY);
    }
    lastDraggingX = draggingX;
    lastDraggingY = draggingY;
  }

  public void setDragEventListener(DragEventListener dragEventListener) {
    this.dragEventListener = dragEventListener;
  }

  public interface DragEventListener {
    void onDragStart();

    /**
     *
     * @param totalDistanceX sum distance that finger has been moved on x axis
     * @param totalDistanceY sum distance that finger has been moved on y axis
     */
    void onDragEnd(float totalDistanceX, float totalDistanceY);

    /**
     *
     * @param currTranslateX the translation on x axis that finger has been moved since the last onDragUpdate
     * @param currTranslateY the translation on y axis that finger has been moved since the last onDragUpdate
     */
    void onDragUpdate(float currTranslateX, float currTranslateY);
  }
}
