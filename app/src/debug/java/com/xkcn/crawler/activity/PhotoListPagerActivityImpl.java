package com.xkcn.crawler.activity;

import android.view.Menu;
import android.view.MenuItem;

import com.xkcn.crawler.R;
import com.xkcn.crawler.activity.PhotoListPagerActivity;
import com.xkcn.crawler.adapter.PhotoListPagerAdapter;
import com.xkcn.crawler.event.RefreshPhotoListingPager;
import com.xkcn.crawler.fragment.DebugOptionsDialog;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 1/14/16.
 */
public class PhotoListPagerActivityImpl extends PhotoListPagerActivity {
    private DebugEventListener debugEventListener;

    @Override
    protected void onStart() {
        super.onStart();

        if (debugEventListener == null) {
            debugEventListener = new DebugEventListener();
        }
        EventBus.getDefault().register(debugEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(debugEventListener);
    }

    @Override
    protected void initTemplateViews() {
        super.initTemplateViews();

        addDebugOptionsDialog();
    }

    private void addDebugOptionsDialog() {
        viewNavigation.getMenu().add(Menu.NONE, R.id.nav_item_debug_options, Menu.NONE, R.string.nav_item_debug);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_item_debug_options) {
            new DebugOptionsDialog().show(getSupportFragmentManager(), null);
            return true;
        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    protected PhotoListPagerAdapter createPhotoListPagerAdapter() {
        return new PhotoListPagerAdapter(getSupportFragmentManager());
    }

    /* event bus */

    private class DebugEventListener {
        public void onEventMainThread(RefreshPhotoListingPager event) {
            presenter.loadPageCount();
        }
    }

    /* END - event bus*/
}
