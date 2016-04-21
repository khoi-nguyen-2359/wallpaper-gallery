package com.xkcn.gallery.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.WindowInsets;

import com.xkcn.gallery.adapter.PhotoListingPagerAdapter;
import com.xkcn.gallery.di.PhotoComponent;
import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoListingViewPagerImpl extends ViewPager implements PhotoListingViewPager {
    private static final int DEF_OFFSCREEN_PAGE = 1;

    private PhotoListingPagerAdapter adapterPhotoPages;

    private PhotoListingViewPagerPresenter presenter;

    private PhotoComponent photoComponent;

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
            adapterPhotoPages = new PhotoListingPagerAdapter(photoComponent);
            setAdapter(adapterPhotoPages);
        }

        adapterPhotoPages.setPageCount(count);
        adapterPhotoPages.setType(type);
        adapterPhotoPages.setPerPage(listingPerPage);
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

    public void setPhotoComponent(PhotoComponent photoComponent) {
        this.photoComponent = photoComponent;
    }

    @Override
    public WindowInsets computeSystemWindowInsets(WindowInsets in, Rect outLocalInsets) {
        return super.computeSystemWindowInsets(in, outLocalInsets);
    }
}
