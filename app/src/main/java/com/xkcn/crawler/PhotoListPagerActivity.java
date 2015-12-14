package com.xkcn.crawler;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.data.PhotoDetailsDataStore;
import com.xkcn.crawler.data.PhotoDetailsSqliteDataStore;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.event.PhotoCrawlingFinishedEvent;
import com.xkcn.crawler.presenter.PhotoListPagerViewPresenter;
import com.xkcn.crawler.view.PhotoListPagerView;

public class PhotoListPagerActivity extends PhotoPagerActivity
        implements NavigationView.OnNavigationItemSelectedListener, PhotoListPagerView {

    private PhotoPagerAdapter adapterPhotoPages;
    private ViewPager pagerPhotoPage;
    private PhotoDetailsDataStore photoDetailsDataStore;
    private PreferenceDataStore prefDataStore;
    private PhotoListPagerViewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initTemplateViews();
        presenter.loadPageCount();
    }

    private void initData() {
        photoDetailsDataStore = new PhotoDetailsSqliteDataStore();
        prefDataStore = new PreferenceDataStoreImpl();

        presenter = new PhotoListPagerViewPresenter(this, photoDetailsDataStore, prefDataStore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initPager(Integer pageCount) {
        adapterPhotoPages = new PhotoPagerAdapter(getSupportFragmentManager(), pageCount);
        adapterPhotoPages.setType(PhotoPagerAdapter.TYPE_LATEST);

        pagerPhotoPage = (ViewPager) findViewById(R.id.pager_photo_page);
        pagerPhotoPage.setOffscreenPageLimit(5);
        pagerPhotoPage.setAdapter(adapterPhotoPages);
    }

    private void initTemplateViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            changePhotoListingType(PhotoPagerAdapter.TYPE_HOTEST);
        } else if (id == R.id.nav_latest) {
            changePhotoListingType(PhotoPagerAdapter.TYPE_LATEST);
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

    public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
        changePhotoListingType(adapterPhotoPages.getType());
    }
}
