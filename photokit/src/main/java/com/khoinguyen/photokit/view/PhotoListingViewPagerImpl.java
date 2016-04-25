package com.khoinguyen.photokit.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.khoinguyen.photokit.adapter.PhotoListingPagerAdapter;
import com.khoinguyen.photokit.presenter.PhotoListingViewPagerPresenter;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoListingViewPagerImpl extends ViewPager implements PhotoListingViewPager {
    private static final int DEF_OFFSCREEN_PAGE = 1;

    private PhotoListingPagerAdapter adapterPhotoPages;

    private PhotoListingViewPagerPresenter presenter;

    public PhotoListingViewPagerImpl(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);
    }

    public PhotoListingViewPagerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void populatePhotoData(int count, final int listingPerPage, final int type) {
        if (adapterPhotoPages == null) {
            adapterPhotoPages = presenter.createPhotoListingPagerAdapter();
            setAdapter(adapterPhotoPages);
        }

        adapterPhotoPages.setPageCount(count);
        adapterPhotoPages.setType(type);
        adapterPhotoPages.notifyDataSetChanged();

        presenter.loadLastWatchedPhotoListPage();
    }

    @Override
    public void displayPage(int page) {
        setCurrentItem(page, false);
    }

    @Override
    public int getCurrentPagePosition() {
        return getCurrentItem();
    }

    @Override
    public PhotoListingView getCurrentPageView() {
        return adapterPhotoPages.getPrimaryPage();
    }

    @Override
    public void changeListingType(int type) {
        adapterPhotoPages.setType(type);
        adapterPhotoPages.notifyDataSetChanged();
        setAdapter(adapterPhotoPages);
    }

    public void setPresenter(PhotoListingViewPagerPresenter presenter) {
        this.presenter = presenter;
    }
}
