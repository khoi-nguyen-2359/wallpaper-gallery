package com.xkcn.crawler;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.photoactions.PhotoDownloadManager;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.util.P;


public class MainActivity extends BaseActivity {

    private PagerAdapter adapterPhotoPages;
    private ViewPager pager;
    private Dialog progDlg;
    private PhotoDownloadManager photoDownloadManager;
    private Dialog proDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
        checkUpdate();
    }

    private void initData() {
        photoDownloadManager = PhotoDownloadManager.getInstance();
    }

    private void initViews() {
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapterPhotoPages = new PhotoPagerAdapter(getSupportFragmentManager()));
    }

    private void checkUpdate() {
        if (P.getLastUpdateTime() < System.currentTimeMillis() - P.PERIOD_UPDATE) {
            UpdateService.startActionUpdate(this);
        }
    }
}
