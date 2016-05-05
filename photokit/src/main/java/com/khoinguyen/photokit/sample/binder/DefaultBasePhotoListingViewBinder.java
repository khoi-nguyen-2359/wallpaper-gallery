package com.khoinguyen.photokit.sample.binder;

import android.view.View;

import com.khoinguyen.photokit.binder.BasePhotoListingViewBinder;
import com.khoinguyen.photokit.ItemViewHolder;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class DefaultBasePhotoListingViewBinder extends BasePhotoListingViewBinder {
    private Map<View, ItemViewHolder<PhotoDisplayInfo>> mapDefaultViewHolder = new HashMap<>();
    private Map<Integer, PhotoDisplayInfo> cachePhotoDisplayInfo = new HashMap<>();

    protected abstract PhotoDisplayInfo createPhotoDisplayInfo(int itemIndex);
    protected abstract ItemViewHolder<PhotoDisplayInfo> createPhotoDisplayItemViewHolder(View itemView);

    @Override
    public ItemViewHolder createItemViewHolder(View itemView) {
        return null;
    }

    @Override
    protected Object createItemData(int itemIndex) {
        return null;
    }

    @Override
    public void bindItemData(View itemView, int itemIndex) {
        super.bindItemData(itemView, itemIndex);

        if (itemView == null) {
            return;
        }

        ItemViewHolder<PhotoDisplayInfo> vhDefault = getDefaultPhotoListingItemViewHolder(itemView);
        PhotoDisplayInfo photoDisplayInfo = getPhotoDisplayInfo(itemIndex);
        if (vhDefault != null) {
            vhDefault.bindItemData(itemIndex, photoDisplayInfo);
        }
    }

    public PhotoDisplayInfo getPhotoDisplayInfo(int itemIndex) {
        PhotoDisplayInfo photoDisplayInfo = cachePhotoDisplayInfo.get(itemIndex);
        if (photoDisplayInfo == null) {
            photoDisplayInfo = createPhotoDisplayInfo(itemIndex);
            cachePhotoDisplayInfo.put(itemIndex, photoDisplayInfo);
        }

        return photoDisplayInfo;
    }

    public ItemViewHolder<PhotoDisplayInfo> getDefaultPhotoListingItemViewHolder(View itemView) {
        if (itemView == null) {
            return null;
        }

        ItemViewHolder<PhotoDisplayInfo> vhDefault = mapDefaultViewHolder.get(itemView);
        if (vhDefault == null) {
            vhDefault = createPhotoDisplayItemViewHolder(itemView);
            mapDefaultViewHolder.put(itemView, vhDefault);
        }

        return vhDefault;
    }
}
