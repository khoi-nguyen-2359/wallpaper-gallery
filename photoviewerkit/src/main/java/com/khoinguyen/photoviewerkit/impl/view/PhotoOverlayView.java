package com.khoinguyen.photoviewerkit.impl.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoOverlayView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 6/22/16.
 */

public class PhotoOverlayView extends FrameLayout implements IPhotoOverlayView<SharedData> {
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

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public PhotoOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.photokit_overlay_view, this);
    mainLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
    tvPhotoDescription = (TextView) findViewById(R.id.tv_photo_description);
    tvPhotoDescription.setMovementMethod(LinkMovementMethod.getInstance());
    viewPhotoAction = (PhotoActionView) findViewById(R.id.view_photo_action);
  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) {
    this.widget = widget;
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
    
    viewPhotoAction.setPhoto(photoDisplayInfo);
  }

  @Override
  public void show() {
    setVisibility(VISIBLE);
  }

  @Override
  public void hide() {
    setVisibility(GONE);
  }
}
