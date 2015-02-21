package com.xkcn.crawler.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xkcn.crawler.fragment.PhotoPageFragment;

/**
 * Created by khoinguyen on 12/23/14.
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
    public static final int MAX_PAGE = 446;
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_LATEST = 2;

    private int type;

    public PhotoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoPageFragment.instantiate(position + 1, type);
    }

    @Override
    public int getCount() {
        return MAX_PAGE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
