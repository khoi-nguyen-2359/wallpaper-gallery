package com.xkcn.gallery.view.activity;

import android.view.Menu;
import android.view.MenuItem;

import com.xkcn.gallery.R;
import com.xkcn.gallery.event.RefreshPhotoListingPager;
import com.xkcn.gallery.view.fragment.DebugOptionsDialog;
import com.xkcn.gallery.imageloader.PhotoFileManager;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by khoinguyen on 1/14/16.
 */
public class MainActivityImpl extends MainActivity {
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

  public PhotoFileManager getPhotoFileManager() {
    return photoFileManager;
  }

    /* event bus */

  private class DebugEventListener {
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefreshPhotoListingPager event) {
      photoListingPresenter.loadPhotoPage(0, PhotoListingViewPresenter.TYPE_LATEST);
    }
  }

    /* END - event bus*/
}
