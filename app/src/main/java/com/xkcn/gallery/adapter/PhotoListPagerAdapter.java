package com.xkcn.gallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xkcn.gallery.fragment.PhotoListPageFragment;

/**
 * Created by khoinguyen on 12/23/14.
 */
public class PhotoListPagerAdapter extends FragmentStatePagerAdapter {
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_LATEST = 2;
    public static final int TYPE_INVALID = -1;

    protected int type;
    protected int pageCount;

    public PhotoListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoListPageFragment.instantiate(position + 1, type);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
