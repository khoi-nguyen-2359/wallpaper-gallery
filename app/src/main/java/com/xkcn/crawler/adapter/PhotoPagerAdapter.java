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

    public PhotoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoPageFragment.instantiate(position + 1);
    }

    @Override
    public int getCount() {
        return MAX_PAGE;
    }
}
