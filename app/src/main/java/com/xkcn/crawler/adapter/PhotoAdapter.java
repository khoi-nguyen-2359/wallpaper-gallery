package com.xkcn.crawler.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xkcn.crawler.R;
import com.xkcn.crawler.SinglePhotoActivity;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.view.PhotoActionsView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public static final float RATIO_SCALE = 1.0f;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ivPhoto;
        PhotoActionsView viewPhotoActions;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            viewPhotoActions = (PhotoActionsView) itemView.findViewById(R.id.view_actions);
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<PhotoDetails> dataPhotos;
    public PhotoAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        dataPhotos = new ArrayList<>();
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

        viewHolder.ivPhoto.setAspectRatio(0.75f);
        viewHolder.ivPhoto.setImageURI(Uri.parse(dataPhotos.get(i).getPhotoHigh()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SinglePhotoActivity.intentViewSinglePhoto(context, dataPhotos.get(i));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPhotos.size();
    }
}
