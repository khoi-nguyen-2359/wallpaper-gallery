package com.xkcn.crawler.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xkcn.crawler.R;
import com.xkcn.crawler.SinglePhotoActivity;
import com.xkcn.crawler.imageloader.RoundedTransformation;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.view.PhotoActionsIconView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public static final float RATIO_SCALE = 1.0f;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        PhotoActionsIconView viewPhotoActions;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            viewPhotoActions = (PhotoActionsIconView) itemView.findViewById(R.id.view_actions);
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<Photo> dataPhotos;
    public PhotoAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        dataPhotos = new ArrayList<>();
    }

    public void setDataPhotos(List<Photo> dataPhotos) {
        this.dataPhotos = dataPhotos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.item_photo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.viewPhotoActions.getPresenter().setPhoto(dataPhotos.get(i));

        Picasso.with(context)
                .load(dataPhotos.get(i).getPhoto500())
                .transform(new RoundedTransformation(5, 1))
//                .transform(new ScaleTransformation(0.8f))
                .into(viewHolder.ivPhoto);

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
