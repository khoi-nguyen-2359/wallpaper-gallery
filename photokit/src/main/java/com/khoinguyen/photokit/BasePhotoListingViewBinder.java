package com.khoinguyen.photokit;

import android.view.View;

import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class BasePhotoListingViewBinder implements PhotoListingViewBinder {
    private Map<View, ItemViewHolder> mapViewHolder = new HashMap<>();
    private Map<Integer, Object> mapDataCache = new HashMap<>();
    private Map<View, ItemViewHolder<PhotoDisplayInfo>> mapDefaultViewHolder = new HashMap<>();
    private Map<Integer, PhotoDisplayInfo> cachePhotoDisplayInfo = new HashMap<>();

    protected abstract PhotoDisplayInfo createPhotoDisplayInfo(int itemIndex);
    protected abstract ItemViewHolder<PhotoDisplayInfo> createDefaultPhotoListingItemViewHolder(View itemView);

    protected Object createItemData(int itemIndex) {
        return null;
    }

    protected ItemViewHolder createItemViewHolder(View itemView) {
        return null;
    }

    @Override
    public void bindItemData(View itemView, int itemIndex) {
        if (itemView == null) {
            return;
        }

        ItemViewHolder vh = getItemViewHolder(itemView);
        Object data = getItemData(itemIndex);
        if (vh != null) {
            vh.bindItemData(itemIndex, data);
        }

        ItemViewHolder<PhotoDisplayInfo> vhDefault = getDefaultPhotoListingItemViewHolder(itemView);
        PhotoDisplayInfo photoDisplayInfo = getPhotoDisplayInfo(itemIndex);
        if (vhDefault != null) {
            vhDefault.bindItemData(itemIndex, photoDisplayInfo);
        }
    }

    @Override
    public Object getItemData(int itemIndex) {
        Object data = mapDataCache.get(itemIndex);
        if (data == null) {
            data = createItemData(itemIndex);
            mapDataCache.put(itemIndex, data);
        }

        return data;
    }

    @Override
    public ItemViewHolder getItemViewHolder(View itemView) {
        if (itemView == null) {
            return null;
        }

        ItemViewHolder vh = mapViewHolder.get(itemView);
        if (vh == null) {
            vh = createItemViewHolder(itemView);
            mapViewHolder.put(itemView, vh);
        }

        return vh;
    }

    @Override
    public PhotoDisplayInfo getPhotoDisplayInfo(int itemIndex) {
        PhotoDisplayInfo photoDisplayInfo = cachePhotoDisplayInfo.get(itemIndex);
        if (photoDisplayInfo == null) {
            photoDisplayInfo = createPhotoDisplayInfo(itemIndex);
            cachePhotoDisplayInfo.put(itemIndex, photoDisplayInfo);
        }

        return photoDisplayInfo;
    }

    @Override
    public ItemViewHolder<PhotoDisplayInfo> getDefaultPhotoListingItemViewHolder(View itemView) {
        if (itemView == null) {
            return null;
        }

        ItemViewHolder<PhotoDisplayInfo> vhDefault = mapDefaultViewHolder.get(itemView);
        if (vhDefault == null) {
            vhDefault = createDefaultPhotoListingItemViewHolder(itemView);
            mapDefaultViewHolder.put(itemView, vhDefault);
        }

        return vhDefault;
    }
}
