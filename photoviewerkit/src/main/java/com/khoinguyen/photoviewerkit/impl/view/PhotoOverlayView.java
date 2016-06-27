package com.khoinguyen.photoviewerkit.impl.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.listingadapter.PhotoActionListingAdapter;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoOverlayView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 6/22/16.
 */

public class PhotoOverlayView extends ViewGroup implements IPhotoOverlayView<SharedData> {
  private TextView tvPhotoDescription;
  private ConstraintLayout mainLayout;
  private PhotoActionView viewPhotoAction;

  private IPhotoViewerKitWidget<SharedData> widget;
  private SharedData sharedData;
  private IEventBus eventBus;

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

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (changed) {
      mainLayout.layout(l, t, r, b);
    }
  }

  private void init() {
    inflate(getContext(), R.layout.photokit_overlay_view, this);
    mainLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
    tvPhotoDescription = (TextView) findViewById(R.id.tv_photo_description);
    viewPhotoAction = (PhotoActionView) findViewById(R.id.view_photo_action);
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) {
    this.widget = widget;
    this.sharedData = widget.getSharedData();
    this.eventBus = widget.getEventBus();
  }

  @Override
  public void setPhotoActionAdapter(PhotoActionListingAdapter actionAdapter) {
    viewPhotoAction.setActionAdapter(actionAdapter);
  }

  @Override
  public void bindPhoto(PhotoDisplayInfo photoDisplayInfo) {
    tvPhotoDescription.setText(photoDisplayInfo.getDescription());
  }
}
