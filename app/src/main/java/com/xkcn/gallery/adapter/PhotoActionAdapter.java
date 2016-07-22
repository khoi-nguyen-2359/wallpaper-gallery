package com.xkcn.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khoinguyen.apptemplate.listing.adapter.PartitionedListingAdapter;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.IViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItem;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.util.SimpleSupplier;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.imageloader.PhotoFileManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhotoActionAdapter extends PartitionedListingAdapter<IViewHolder<PhotoDisplayInfo>> {
  public static final int TYPE_SET_WALLPAPER = 2;
  public static final int TYPE_SHARE = 1;
  public static final int TYPE_DOWNLOAD = 3;

  @Inject
  PhotoFileManager photoFileManager;

  public PhotoActionAdapter() {
    registerListingItemType(new PhotoActionAdapter.ShareItemType());
    registerListingItemType(new PhotoActionAdapter.SetWallpaperItemType());
    registerListingItemType(new PhotoActionAdapter.DownloadItemType());
    // call this right here because this adapter has a static data set from beginning
    updateDataSet();
  }

  @Override
  public int getItemId(int itemIndex) {
    return getViewType(itemIndex);
  }

  @Override
  protected List<ListingItem> createDataSet() {
    List<ListingItem> allItems = new ArrayList<>();
    ListingItem shareItem = new ListingItem(null, TYPE_SHARE);
    allItems.add(shareItem);
    ListingItem setWallpaperItem = new ListingItem(null, TYPE_SET_WALLPAPER);
    allItems.add(setWallpaperItem);
    ListingItem downloadItem = new ListingItem(null, TYPE_DOWNLOAD);
    allItems.add(downloadItem);
    return allItems;
  }

  public static class ShareItemType extends ListingItemType {
    public ShareItemType() {
      super(TYPE_SHARE);
    }

    @Override
    public View createView(ViewGroup container) {
      LayoutInflater layoutInflater = getLayoutInflater(container.getContext());

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
      LayoutInflater layoutInflater = getLayoutInflater(container.getContext());
      return layoutInflater.inflate(R.layout.photo_action_set_wallpaper, container, false);
    }

    @Override
    public IViewHolder createViewHolder(View view) {
      return new BaseViewHolder(view);
    }
  }

  public static class DownloadItemType extends ListingItemType<IViewHolder<PhotoDisplayInfo>> {

    public DownloadItemType() {
      super(TYPE_DOWNLOAD);
    }

    @Override
    public View createView(ViewGroup container) {
      return getLayoutInflater(container.getContext()).inflate(R.layout.photo_action_download, container, false);
    }

    @Override
    public IViewHolder<PhotoDisplayInfo> createViewHolder(View view) {
      return new DownloadButtonViewHolder(view);
    }

    private class DownloadButtonViewHolder extends BaseViewHolder<PhotoDisplayInfo> {
      private TextView tvDownload;

      public DownloadButtonViewHolder(View view) {
        super(view);

        tvDownload = (TextView) view.findViewById(R.id.tv_download);
      }

      @Override
      public void bind(PhotoDisplayInfo photoDisplayInfo) {
        if (photoDisplayInfo.getLocalFile().exists()) {
          tvDownload.setText(R.string.photo_action_downloaded_already);
          tvDownload.setEnabled(false);
        } else {
          tvDownload.setText(R.string.photo_action_download);
          tvDownload.setEnabled(true);
        }
      }
    }
  }
}