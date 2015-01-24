package com.xkcn.crawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.event.SelectPhotoEvent;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.util.U;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    private PagerAdapter adapterPhotoPages;
    private ViewPager pager;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

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

    public void onEvent(SelectPhotoEvent e) {
        Intent i = SinglePhotoActivity.intentViewSinglePhoto(this, e.getPhoto());
        startActivity(i);
    }
}
