package com.khoinguyen.photokit.sample.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.photokit.BasePhotoListingViewBinder;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.ItemViewHolder;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 5/2/16.
 */
public abstract class DefaultPhotoGalleryViewBinder extends BasePhotoListingViewBinder {
    private LayoutInflater layoutInflater;

    @Override
    public View getItemView(ViewGroup container, int itemIndex) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(container.getContext());
        }

        ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photokit_photo_gallery_pager_item, container, false);
        itemView.getHierarchy().setFadeDuration(0);
        itemView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        return itemView;
    }

    @Override
    public ItemViewHolder<PhotoDisplayInfo> createDefaultPhotoListingItemViewHolder(View itemView) {
        return new PhotoGalleryItemItemViewHolder((ZoomableDraweeView) itemView);
    }

    private static class PhotoGalleryItemItemViewHolder extends ItemViewHolder<PhotoDisplayInfo> {
        private ZoomableDraweeView itemView;

        private PhotoGalleryItemItemViewHolder(ZoomableDraweeView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        @Override
        public void bindItemData(int itemIndex, PhotoDisplayInfo data) {
            super.bindItemData(itemIndex, data);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(data.getLowResUri()))
                    .setImageRequest(ImageRequest.fromUri(data.getHighResUri()))
                    .setOldController(itemView.getController())
                    .setCallerContext(this)
                    .build();
            itemView.setController(controller);
        }
    }
}
