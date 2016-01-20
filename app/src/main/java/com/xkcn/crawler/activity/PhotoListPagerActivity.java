package com.xkcn.crawler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xkcn.crawler.R;
import com.xkcn.crawler.adapter.PhotoListPagerAdapter;
import com.xkcn.crawler.data.PhotoDetailsDataStore;
import com.xkcn.crawler.data.PhotoDetailsSqliteDataStore;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.event.OnPhotoListItemClicked;
import com.xkcn.crawler.event.PhotoCrawlingFinishedEvent;
import com.xkcn.crawler.presenter.PhotoListPagerViewPresenter;
import com.xkcn.crawler.view.PhotoListPagerView;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class PhotoListPagerActivity extends PhotoPagerActivity
        implements NavigationView.OnNavigationItemSelectedListener, PhotoListPagerView {

    protected static final int PHOTO_TYPE_DEFAULT = PhotoListPagerAdapter.TYPE_LATEST;
    private static final int DEF_OFFSCREEN_PAGE = 4;

    protected PhotoListPagerAdapter adapterPhotoPages;
    protected PhotoDetailsDataStore photoDetailsDataStore;
    protected PreferenceDataStore prefDataStore;
    protected PhotoListPagerViewPresenter presenter;

    @Bind(R.id.pager_photo_page) ViewPager pagerPhotoPage;
    @Bind(R.id.nav_view) NavigationView viewNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initTemplateViews();
        initViews();
        presenter.loadPageCount();
    }

    private void initViews() {
        pagerPhotoPage.addOnPageChangeListener(onPhotoListPageChanged);
        pagerPhotoPage.setAdapter(adapterPhotoPages);
        pagerPhotoPage.setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);
    }

    private void initData() {
        photoDetailsDataStore = new PhotoDetailsSqliteDataStore();
        prefDataStore = new PreferenceDataStoreImpl();

        presenter = new PhotoListPagerViewPresenter(this, photoDetailsDataStore, prefDataStore);
    }

    @Override
    public void setupPagerAdapter(int pageCount, int type) {
        if (adapterPhotoPages == null) {
            adapterPhotoPages = createPhotoListPagerAdapter();
            pagerPhotoPage.setAdapter(adapterPhotoPages);
        }

        if (pageCount != adapterPhotoPages.getCount() || type != adapterPhotoPages.getType()) {
            adapterPhotoPages.setPageCount(pageCount);
            adapterPhotoPages.setType(type);
            adapterPhotoPages.notifyDataSetChanged();
        }
    }

    protected PhotoListPagerAdapter createPhotoListPagerAdapter() {
        return new PhotoListPagerAdapter(getSupportFragmentManager());
    }

    private ViewPager.OnPageChangeListener onPhotoListPageChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    protected void initTemplateViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        viewNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hotest) {
            changePhotoListingType(PhotoListPagerAdapter.TYPE_HOTEST);
        } else if (id == R.id.nav_latest) {
            changePhotoListingType(PhotoListPagerAdapter.TYPE_LATEST);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePhotoListingType(int type) {
        adapterPhotoPages.setType(type);
        adapterPhotoPages.notifyDataSetChanged();
        pagerPhotoPage.setAdapter(adapterPhotoPages);
    }

    @Override
    public int getCurrentType() {
        return adapterPhotoPages != null ? adapterPhotoPages.getType() : PHOTO_TYPE_DEFAULT;
    }

    /*** event bus ***/

    public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
        presenter.loadPageCount();
    }

    public void onEventMainThread(OnPhotoListItemClicked event) {
        Intent intent = PhotoSinglePagerActivity.intentViewSinglePhoto(this, adapterPhotoPages.getType(), pagerPhotoPage.getCurrentItem()+1, event.getClickedPosition());
        startActivity(intent);
    }

    /*** end - event bus ***/
}
