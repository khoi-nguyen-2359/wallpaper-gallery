package com.xkcn.crawler;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.photoactions.PhotoDownloadManager;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.util.P;
import com.xkcn.crawler.view.SidebarView;


public class MainActivity extends BaseActivity {

    private PhotoPagerAdapter adapterPhotoPages;
    private ViewPager pager;
    private SidebarView sidebar;
    private DrawerLayout layoutDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
        checkUpdate();
    }

    private void initData() {
    }

    private void initViews() {
        layoutDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        adapterPhotoPages = new PhotoPagerAdapter(getSupportFragmentManager());
        adapterPhotoPages.setType(PhotoPagerAdapter.TYPE_HOTEST);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapterPhotoPages);

        sidebar = (SidebarView) findViewById(R.id.sidebar);
        sidebar.setOnSidebarItemClick(onSidebarItemClick);
    }

    private void checkUpdate() {
        if (P.getLastUpdateTime() < System.currentTimeMillis() - P.PERIOD_UPDATE) {
            UpdateService.startActionUpdate(this);
        }
    }

    public void emptyOnClick(View view) {
        layoutDrawer.closeDrawers();
    }

    private View.OnClickListener onSidebarItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_hotest: {
                    adapterPhotoPages.setType(PhotoPagerAdapter.TYPE_HOTEST);
                    adapterPhotoPages.notifyDataSetChanged();
                    pager.setAdapter(adapterPhotoPages);
                    break;
                }

                case R.id.tv_latest: {
                    adapterPhotoPages.setType(PhotoPagerAdapter.TYPE_LATEST);
                    adapterPhotoPages.notifyDataSetChanged();
                    pager.setAdapter(adapterPhotoPages);
                    break;
                }
            }

            layoutDrawer.closeDrawers();
        }
    };
}
