package com.xkcn.crawler;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.event.SetWallpaperClicked;
import com.xkcn.crawler.photoactions.PhotoDownloadManager;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.util.U;
import com.xkcn.crawler.util.UiUtils;

import de.greenrobot.event.EventBus;


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
        if (U.getLastUpdate() < System.currentTimeMillis() - U.PERIOD_UPDATE) {
            UpdateService.startActionUpdate(this);
        }
    }
}
