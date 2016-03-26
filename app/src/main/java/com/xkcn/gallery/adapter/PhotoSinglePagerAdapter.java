package com.xkcn.gallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xkcn.gallery.fragment.PhotoSinglePageFragment;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.view.PhotoSinglePagerView;

import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoSinglePagerAdapter extends FragmentStatePagerAdapter {
    private List<PhotoDetails> photoListPage;

    public PhotoSinglePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoSinglePageFragment.instantiate(photoListPage.get(position));
    }

    @Override
    public int getCount() {
        return photoListPage==null?0:photoListPage.size();
    }

    public void setPhotoDatas(List<PhotoDetails> photoListPage) {
        this.photoListPage = photoListPage;
    }

    public List<PhotoDetails> getPhotoListPage() {
        return photoListPage;
    }
}
