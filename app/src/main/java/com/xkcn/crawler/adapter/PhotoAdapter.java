package com.xkcn.crawler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xkcn.crawler.R;
import com.xkcn.crawler.event.SelectPhotoEvent;
import com.xkcn.crawler.imageloader.RoundedTransformation;
import com.xkcn.crawler.db.Photo;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public static final float RATIO_SCALE = 1.0f;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
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

        Picasso.with(context)
                .load(dataPhotos.get(i).getPhoto500())
                .transform(new RoundedTransformation(10, 1))
//                .transform(new ScaleTransformation(0.8f))
                .into(viewHolder.ivPhoto);

        viewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectPhotoEvent(dataPhotos.get(i)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPhotos.size();
    }
}
