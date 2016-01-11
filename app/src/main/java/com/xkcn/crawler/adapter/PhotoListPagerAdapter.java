package com.xkcn.crawler.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xkcn.crawler.fragment.PhotoListPageFragment;

/**
 * Created by khoinguyen on 12/23/14.
 */
public class PhotoListPagerAdapter extends FragmentStatePagerAdapter {
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_LATEST = 2;

    private int type;
    private int pageCount;

    public PhotoListPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.pageCount = pageCount;
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
}
