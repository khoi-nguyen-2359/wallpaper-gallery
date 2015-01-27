package com.xkcn.crawler;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.util.U;


public class MainActivity extends ActionBarActivity {

    private PagerAdapter adapterPhotoPages;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkUpdate();
    }

    private void initViews() {
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapterPhotoPages = new PhotoPagerAdapter(getSupportFragmentManager()));
    }

    private void checkUpdate() {
        if (U.getLastUpdate() < System.currentTimeMillis() - U.PERIOD_UPDATE) {
            UpdateService.startActionUpdate(this);
        }
    }
}
