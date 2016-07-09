package com.xkcn.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoActionAdapter extends PartitionedListingAdapter {
  public static final int TYPE_SET_WALLPAPER = 2;
  public static final int TYPE_SHARE = 1;
  public static final int TYPE_DOWNLOAD = 3;

  @Override
  public int getItemId(int itemIndex) {
    int actionId = getViewType(itemIndex);
    L.get().d("actionId=%d", actionId);
    return actionId;
  }

  @Override
  protected List<ListingItem> createDataSet() {
    List<ListingItem> allItems = new ArrayList<>();
    ListingItem shareItem = new ListingItem(null, getListingItemType(TYPE_SHARE));
    allItems.add(shareItem);
    ListingItem setWallpaperItem = new ListingItem(null, getListingItemType(TYPE_SET_WALLPAPER));
    allItems.add(setWallpaperItem);
    ListingItem downloadItem = new ListingItem(null, getListingItemType(TYPE_DOWNLOAD));
    allItems.add(downloadItem);
    return allItems;
  }

  public static class ShareItemType extends ListingItemType {

    public ShareItemType() {
      super(TYPE_SHARE);
    }

    @Override
    public View createView(ViewGroup container) {
      LayoutInflater layoutInflater = getLayoutInflater(container);

      return layoutInflater.inflate(R.layout.photo_action_share, container, false);
    }

    @Override
    public IViewHolder createViewHolder(View view) {
      return new BaseViewHolder(view);
    }
  }

  public static class SetWallpaperItemType extends ListingItemType {

    public SetWallpaperItemType() {
      super(TYPE_SET_WALLPAPER);
    }

    @Override
    public View createView(ViewGroup container) {
      LayoutInflater layoutInflater = getLayoutInflater(container);
      return layoutInflater.inflate(R.layout.photo_action_set_wallpaper, container, false);
    }

    @Override
    public IViewHolder createViewHolder(View view) {
      return new BaseViewHolder(view);
    }
  }

  public static class DownloadItemType extends ListingItemType {

    public DownloadItemType() {
      super(TYPE_DOWNLOAD);
    }

    @Override
    public View createView(ViewGroup container) {
      return getLayoutInflater(container).inflate(R.layout.photo_action_download, container, false);
    }

    @Override
    public IViewHolder createViewHolder(View view) {
      return new BaseViewHolder(view);
    }
  }
}