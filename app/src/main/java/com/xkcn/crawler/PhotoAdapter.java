package com.xkcn.crawler;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

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
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        Picasso.with(context)
                .load(dataPhotos.get(i).getPhoto500())
                .transform(new RoundedTransformation(10, 1))
//                .transform(new ScaleTransformation(0.8f))
                .into(viewHolder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return dataPhotos.size();
    }
}
