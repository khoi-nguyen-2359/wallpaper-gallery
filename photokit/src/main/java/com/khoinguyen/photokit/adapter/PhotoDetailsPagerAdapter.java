package com.khoinguyen.photokit.adapter;

import android.support.v4.view.PagerAdapter;
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
import com.khoinguyen.photokit.R;
import com.khoinguyen.photokit.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoDetailsPagerAdapter extends PagerAdapter {
    private List<PhotoDetails> photoListPage;
    private LayoutInflater layoutInflater;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(container.getContext());
        }

        ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photo_details_pager_item, container, false);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(container.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
        itemView.setHierarchy(hierarchy);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(photoListPage.get(position).getLowResUri()))
                .setImageRequest(ImageRequest.fromUri(photoListPage.get(position).getHighResUri()))
                .setOldController(itemView.getController())
                .setCallerContext(this)
                .build();
        itemView.setController(controller);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return photoListPage==null?0:photoListPage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setPhotoDatas(List<PhotoDetails> photoListPage) {
        this.photoListPage = photoListPage;
    }

    public List<PhotoDetails> getPhotoListPage() {
        return photoListPage;
    }
}
