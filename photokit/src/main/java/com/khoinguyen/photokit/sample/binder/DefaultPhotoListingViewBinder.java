package com.khoinguyen.photokit.sample.binder;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.photokit.ItemViewHolder;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;
import com.khoinguyen.photokit.BasePhotoListingViewBinder;

/**
 * Created by khoinguyen on 4/29/16.
 */
public abstract class DefaultPhotoListingViewBinder extends BasePhotoListingViewBinder {
    protected LayoutInflater layoutInflater;

    @Override
    public View getItemView(ViewGroup container, int itemIndex) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(container.getContext());
        }

        return layoutInflater.inflate(R.layout.photokit_photo_list_pager_photo_item, container, false);
    }

    @Override
    public ItemViewHolder createItemViewHolder(View itemView) {
        return null;
    }

    @Override
    protected Object createItemData(int itemIndex) {
        return null;
    }

    @Override
    public ItemViewHolder<PhotoDisplayInfo> createDefaultPhotoListingItemViewHolder(View itemView) {
        return new DefaultPhotoListingItemItemViewHolder(itemView);
    }

    public static class DefaultPhotoListingItemItemViewHolder extends ItemViewHolder<PhotoDisplayInfo> {
        protected final SimpleDraweeView ivPhoto;

        public DefaultPhotoListingItemItemViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.photokit_listing_photo_item);
            GenericDraweeHierarchy photoHierarchy = GenericDraweeHierarchyBuilder.newInstance(itemView.getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFadeDuration(0)
                    .build();
            ivPhoto.setHierarchy(photoHierarchy);
        }

        @Override
        public void bindItemData(int itemIndex, PhotoDisplayInfo data) {
            super.bindItemData(itemIndex, data);

            ivPhoto.setAspectRatio(1.5f);
            ivPhoto.setImageURI(Uri.parse(data.getLowResUrl()));
        }
    }
}
