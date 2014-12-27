package com.xkcn.crawler;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private PagerAdapter adapterPhotoPages;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkToUpdate();
    }

    private void initViews() {
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new PhotoPageAdapter(getSupportFragmentManager()));
    }

    private void checkToUpdate() {
        if (U.getLastUpdate() < System.currentTimeMillis() - U.MILISEC_A_DAY) {
            UpdateService.startActionUpdate(this);
        }
    }
}
