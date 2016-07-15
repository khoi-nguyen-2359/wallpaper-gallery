package com.khoinguyen.photoviewerkit.impl.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.eventbus.Subscribe;
import com.khoinguyen.apptemplate.listing.adapter.DataObserver;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitComponent;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionView extends LinearLayout implements IPhotoViewerKitComponent<SharedData> {
  public static final int TYPE_ICON = 0;
  public static final int TYPE_ICON_TEXT = 1;

  private int type;

  private PhotoDisplayInfo photo;
  private IEventBus eventBus;
  private SharedData sharedData;
  private IListingAdapter listingAdapter;
  private PhotoActionEventListener eventListener;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PhotoActionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  public PhotoActionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  public PhotoActionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public PhotoActionView(Context context) {
    super(context);
    init(context, null);
  }

  private void init(Context context, AttributeSet attrs) {
    if (attrs == null) {
      return;
    }

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PhotoActionView);
    type = a.getInt(R.styleable.PhotoActionView_type, 0);
    a.recycle();

    setOrientation(HORIZONTAL);
    setGravity(Gravity.CENTER_HORIZONTAL);
    setShowDividers(SHOW_DIVIDER_MIDDLE);
    Drawable divider = getResources().getDrawable(R.drawable.photoaction_item_divider);
    setDividerDrawable(divider);
  }

  public void bindPhoto(PhotoDisplayInfo photo) {
    this.photo = photo;
    populateAdapterItems();
  }

//  private void initViews() {
//    LayoutInflater inflater = LayoutInflater.from(getContext());
//    if (type == TYPE_ICON) {
//      inflater.inflate(R.layout.view_photo_actions_icon, this, true);
//    } else if (type == TYPE_ICON_TEXT) {
//      inflater.inflate(R.layout.view_photo_actions_text, this, true);
//    }
//  }

  @Override
  public void attach(IPhotoViewerKitWidget<SharedData> widget) {
    eventBus = widget.getEventBus();
    sharedData = widget.getSharedData();
  }

  public void setActionAdapter(IListingAdapter listingAdapter) {
    if (this.listingAdapter != null) {
      this.listingAdapter.unregisterDataObserver(itemDataObserver);
    }
    this.listingAdapter = listingAdapter;
    if (this.listingAdapter != null) {
      this.listingAdapter.registerDataObserver(itemDataObserver);
    }
  }

  private DataObserver itemDataObserver = new DataObserver() {
    @Override
    public void onChanged() {
      populateAdapterItems();
    }
  };

  private void populateAdapterItems() {
    if (listingAdapter == null) {
      return;
    }

    final int nItem = listingAdapter.getCount();
    for (int i = 0; i < nItem; ++i) {
      int viewType = listingAdapter.getViewType(i);
      View currentChildView = getChildAt(i);
      IViewHolder currentViewHolder = currentChildView == null ? null : (IViewHolder) currentChildView.getTag(R.id.photoaction_tag_viewholder);
      View itemView = currentChildView;
      if (currentViewHolder == null || currentViewHolder.getViewType() != viewType) {
        itemView = addAdapterItemView(viewType);
      }

      bindAdapterItemView(i, itemView);
    }
  }

  private void bindAdapterItemView(int i, View itemView) {
    Object data = listingAdapter.getData(i);
    IViewHolder viewHolder = (IViewHolder) itemView.getTag(R.id.photoaction_tag_viewholder);
    viewHolder.bind(data);
    itemView.setTag(R.id.photoaction_tag_id, listingAdapter.getItemId(i));
    // TODO: 6/29/16 what if the itemView has been setOnClickListener already?
    itemView.setOnClickListener(onItemClicked);
  }

  private OnClickListener onItemClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      triggerOnItemSelect(v.getTag(R.id.photoaction_tag_id));
    }
  };

  private void triggerOnItemSelect(Object actionId) {
    if (eventListener == null || !(actionId instanceof Integer)) {
      return;
    }

    eventListener.onPhotoActionSelect((Integer) actionId, photo);
  }

  private View addAdapterItemView(int viewType) {
    View itemView = listingAdapter.getView(this, viewType);
    IViewHolder viewHolder = listingAdapter.getViewHolder(itemView, viewType);
    itemView.setTag(R.id.photoaction_tag_viewholder, viewHolder);
    addView(itemView);

    return itemView;
  }

  public void setEventListener(PhotoActionEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public interface PhotoActionEventListener {
    void onPhotoActionSelect(int actionId, PhotoDisplayInfo photo);
  }
}
