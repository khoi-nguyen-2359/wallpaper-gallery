package com.xkcn.crawler.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xkcn.crawler.fragment.PhotoSinglePageFragment;
import com.xkcn.crawler.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 12/14/15.
 */
public class PhotoSinglePagerAdapter extends FragmentStatePagerAdapter {
    private List<PhotoDetails> photoListPage;

    public PhotoSinglePagerAdapter(FragmentManager fm, List<PhotoDetails> photoListPage) {
        super(fm);
        this.photoListPage = photoListPage;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoSinglePageFragment.instantiate(photoListPage.get(position));
    }

    @Override
    public int getCount() {
        return photoListPage==null?0:photoListPage.size();
    }
}
