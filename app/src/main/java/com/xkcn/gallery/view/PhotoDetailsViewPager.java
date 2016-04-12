package com.xkcn.gallery.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.xkcn.gallery.adapter.PhotoDetailsPagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.view.PhotoListingView;

import java.util.List;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoDetailsViewPager extends ViewPager implements PhotoListingView {
    private PhotoDetailsPagerAdapter adapterPhotoDetails;
    private PhotoListingViewPresenter presenter;

    public PhotoDetailsViewPager(Context context) {
        super(context);
    }

    public PhotoDetailsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void displayPhotoData(List<PhotoDetails> photos) {
        if (adapterPhotoDetails == null) {
            adapterPhotoDetails = new PhotoDetailsPagerAdapter();
            setAdapter(adapterPhotoDetails);
        }

        adapterPhotoDetails.setPhotoDatas(photos);
        adapterPhotoDetails.notifyDataSetChanged();
    }

    public void setPresenter(PhotoListingViewPresenter presenter) {
        this.presenter = presenter;
    }

    public PhotoListingViewPresenter getPresenter() {
        return presenter;
    }
}
