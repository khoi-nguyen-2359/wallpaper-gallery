package com.khoinguyen.photokit.sample.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.photokit.BasePhotoListingViewBinder;
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.ViewHolder;
import com.khoinguyen.photokit.sample.model.PhotoDisplayInfo;

/**
 * Created by khoinguyen on 5/2/16.
 */
public abstract class DefaultPhotoGalleryViewBinder<T> extends DefaultPhotoListingViewBinder<T> {
    private LayoutInflater layoutInflater;

    @Override
    public View createItemView(ViewGroup container, int itemIndex) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(container.getContext());
        }

        ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photokit_photo_gallery_pager_item, container, false);
//        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(container.getResources())
//                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
//                .setFadeDuration(0)
//                .build();
        itemView.getHierarchy().setFadeDuration(0);
        itemView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        return itemView;
    }

    @Override
    public ViewHolder<PhotoDisplayInfo> createDefaultPhotoListingItemViewHolder(View itemView) {
        return new PhotoGalleryItemViewHolder((ZoomableDraweeView) itemView);
    }

    private static class PhotoGalleryItemViewHolder implements ViewHolder<PhotoDisplayInfo> {
        private ZoomableDraweeView itemView;

        private PhotoGalleryItemViewHolder(ZoomableDraweeView itemView) {
            this.itemView = itemView;
        }

        @Override
        public void bind(PhotoDisplayInfo data) {
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
