package com.khoinguyen.photokit.sample.binder;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.ViewHolder;
import com.khoinguyen.photokit.BasePhotoListingViewBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class DefaultPhotoListingViewBinder<T> extends BasePhotoListingViewBinder<T> {
    private Map<View, ViewHolder<PhotoDisplayInfo>> mapDefaultViewHolder = new HashMap<>();
    protected LayoutInflater layoutInflater;
    private Map<Integer, PhotoDisplayInfo> cachePhotoDisplayInfo = new HashMap<>();

    protected abstract PhotoDisplayInfo createPhotoDisplayInfo(int itemIndex);

    public PhotoDisplayInfo getPhotoDisplayInfo(int itemIndex) {
        PhotoDisplayInfo photoDisplayInfo = cachePhotoDisplayInfo.get(itemIndex);
        if (photoDisplayInfo == null) {
            photoDisplayInfo = createPhotoDisplayInfo(itemIndex);
            cachePhotoDisplayInfo.put(itemIndex, photoDisplayInfo);
        }

        return photoDisplayInfo;
    }

    @Override
    public View createItemView(ViewGroup container, int itemIndex) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(container.getContext());
        }

        return layoutInflater.inflate(R.layout.photokit_photo_list_pager_photo_item, container, false);
    }

    @Override
    public void bindItemData(View itemView, int itemIndex) {
        super.bindItemData(itemView, itemIndex);

        if (itemView == null) {
            return;
        }

        ViewHolder<PhotoDisplayInfo> vhDefault = mapDefaultViewHolder.get(itemView);
        if (vhDefault == null) {
            vhDefault = createDefaultPhotoListingItemViewHolder(itemView);
            mapDefaultViewHolder.put(itemView, vhDefault);
        }

        PhotoDisplayInfo photoDisplayInfo = getPhotoDisplayInfo(itemIndex);
        if (vhDefault != null) {
            vhDefault.bind(photoDisplayInfo);
        }
    }

    public ViewHolder<PhotoDisplayInfo> createDefaultPhotoListingItemViewHolder(View itemView) {
        return new DefaultPhotoListingItemViewHolder(itemView);
    }

    @Override
    public ViewHolder createItemViewHolder(View itemView) {
        return null;
    }

    @Override
    public T createItemData(int itemIndex) {
        return null;
    }

    private class DefaultPhotoListingItemViewHolder implements ViewHolder<PhotoDisplayInfo> {
        private final SimpleDraweeView ivPhoto;

        public DefaultPhotoListingItemViewHolder(View itemView) {
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            GenericDraweeHierarchy photoHierarchy = GenericDraweeHierarchyBuilder.newInstance(itemView.getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFadeDuration(0)
                    .build();
            ivPhoto.setHierarchy(photoHierarchy);
        }

        @Override
        public void bind(PhotoDisplayInfo data) {
            ivPhoto.setAspectRatio(1.5f);
            ivPhoto.setImageURI(Uri.parse(data.getLowResUrl()));
        }
    }
}
