package com.xkcn.crawler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.db.PhotoTagDao;
import com.xkcn.crawler.event.UpdateFinishedEvent;
import com.xkcn.crawler.service.UpdateService;
import com.xkcn.crawler.view.SidebarView;

import java.util.HashSet;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {

    private PhotoPagerAdapter adapterPhotoPages;
    private ViewPager pager;
    private SidebarView sidebar;
    private DrawerLayout layoutDrawer;
    private PreferenceDataStore prefDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
//        updateTagCloud();
        checkUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        regEventBus();
    }

    @Override
    protected void onStop() {
        unregEventBus();

        super.onStop();
    }

    private void unregEventBus() {
        EventBus.getDefault().unregister(sidebar);
    }

    private void regEventBus() {
        EventBus.getDefault().register(sidebar);
    }

    private void initData() {
    }

    private void initViews() {
        layoutDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        adapterPhotoPages = new PhotoPagerAdapter(getSupportFragmentManager());
        adapterPhotoPages.setType(PhotoPagerAdapter.TYPE_LATEST);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapterPhotoPages);

        sidebar = (SidebarView) findViewById(R.id.sidebar);
        sidebar.setOnSidebarItemActivated(onSidebarItemClick);

        prefDataStore = new PreferenceDataStoreImpl();
    }

    private void checkUpdate() {
        if (prefDataStore.getLastPhotoCrawlTime() < System.currentTimeMillis() - PreferenceDataStoreImpl.PERIOD_UPDATE) {
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

    public void onEventMainThread(UpdateFinishedEvent event) {
//        updateTagCloud();
    }

    private void updateTagCloud() {
        new AsyncTask<Void, Void, HashSet<String>>() {
            @Override
            protected HashSet<String> doInBackground(Void... params) {
                return PhotoTagDao.queryTagsGroupByMark();
            }

            @Override
            protected void onPostExecute(HashSet<String> strings) {
                super.onPostExecute(strings);
//                sidebar.setTags(strings);
            }
        }.execute();
    }
}
