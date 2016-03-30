package com.xkcn.gallery.event;

import android.view.View;

import com.xkcn.gallery.adapter.PhotoListItemAdapter;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class OnPhotoListItemClicked {
    private int clickedPosition;
    private PhotoListItemAdapter.ViewHolder clickedView;

    public OnPhotoListItemClicked(int clickedPosition, PhotoListItemAdapter.ViewHolder v) {
        this.clickedPosition = clickedPosition;
        clickedView = v;
    }

    public int getItemPosition() {
        return clickedPosition;
    }

    public PhotoListItemAdapter.ViewHolder getItemViewHolder() {
        return clickedView;
    }
}
