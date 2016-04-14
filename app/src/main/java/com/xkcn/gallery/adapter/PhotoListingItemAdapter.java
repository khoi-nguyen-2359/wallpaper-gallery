package com.xkcn.gallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.logging.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoListingItemAdapter extends RecyclerView.Adapter<PhotoListingItemAdapter.ViewHolder> {

    public static final float RATIO_SCALE = 1.0f;
    private L logger;

    private final LayoutInflater inflater;
    private List<PhotoDetails> dataPhotos;
    private View.OnClickListener onItemViewClicked;

    public PhotoListingItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        dataPhotos = new ArrayList<>();
        logger = L.get(getClass().getSimpleName());
    }

    public List<PhotoDetails> getDataPhotos() {
        return dataPhotos;
    }

    public void setDataPhotos(List<PhotoDetails> dataPhotos) {
        this.dataPhotos = dataPhotos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.photo_list_pager_photo_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.ivPhoto.setAspectRatio(1.5f);
        viewHolder.ivPhoto.setImageURI(Uri.parse(dataPhotos.get(i).getLowResUrl()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemViewClicked != null) {
                    onItemViewClicked.onClick(v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPhotos.size();
    }

    public void setOnItemViewClicked(View.OnClickListener onItemViewClicked) {
        this.onItemViewClicked = onItemViewClicked;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            GenericDraweeHierarchy photoHierarchy = GenericDraweeHierarchyBuilder.newInstance(itemView.getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFadeDuration(0)
                    .build();
            ivPhoto.setHierarchy(photoHierarchy);
        }
    }
}
