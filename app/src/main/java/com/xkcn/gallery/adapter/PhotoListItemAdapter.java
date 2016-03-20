package com.xkcn.gallery.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fantageek.toolkit.util.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.event.OnPhotoListItemClicked;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.view.PhotoActionsView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoListItemAdapter extends RecyclerView.Adapter<PhotoListItemAdapter.ViewHolder> {

    public static final float RATIO_SCALE = 1.0f;
    private L logger;

    public List<PhotoDetails> getDataPhotos() {
        return dataPhotos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ivPhoto;
        PhotoActionsView viewPhotoActions;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            PointF focusPoint = new PointF(0.5f, 0.4f);
            ivPhoto.getHierarchy().setActualImageFocusPoint(focusPoint);
            viewPhotoActions = (PhotoActionsView) itemView.findViewById(R.id.view_actions);
        }
    }

    private final LayoutInflater inflater;
    private List<PhotoDetails> dataPhotos;

    public PhotoListItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        dataPhotos = new ArrayList<>();
        logger = L.get(getClass().getSimpleName());
    }

    public void setDataPhotos(List<PhotoDetails> dataPhotos) {
        this.dataPhotos = dataPhotos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.item_photo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.viewPhotoActions.bind(dataPhotos.get(i));

        viewHolder.ivPhoto.setAspectRatio(1.5f);
        viewHolder.ivPhoto.setImageURI(Uri.parse(dataPhotos.get(i).getPhoto500()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OnPhotoListItemClicked(i));
                logger.d("OnPhotoListItemClicked %d", i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPhotos.size();
    }
}
