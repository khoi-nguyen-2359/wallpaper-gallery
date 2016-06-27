package com.khoinguyen.photoviewerkit.interfaces;

import android.graphics.RectF;
import android.net.Uri;

import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 6/27/16.
 */

public interface IPhotoTransitionView<D> extends IPhotoViewerKitComponent<D> {
  void startRevealAnimation(RectF itemRect, RectF fullRect);

  void show();
  void dislayPhoto(PhotoDisplayInfo photo);
}
