package com.xkcn.gallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.model.NavigationItem;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.view.interfaces.MainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class MainActivity extends BaseActivity implements MainView, NavigationView.OnNavigationItemSelectedListener {

	private static final String EXTRAS_NAV_ITEM = "EXTRAS_NAV_ITEM";

	@BindView(R.id.nav_view)
	NavigationView viewNavigation;

	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;

	L log = L.get(this);

	protected MainViewPresenter mainViewPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initMainViews();
		initNavigationItems();
		analyticsCollection.trackListingScreenView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mainViewPresenter.checkToCrawlPhoto();
		EventBus.getDefault().register(this);

	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (!handleDrawerLayoutBackPressed()) {
			super.onBackPressed();
		}
	}

	private boolean handleDrawerLayoutBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
			return true;
		}

		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		handleNavigationItemIntent(intent);
	}

	private void initData() {
		mainViewPresenter = new MainViewPresenter(remoteConfigManager, localConfigManager);
		mainViewPresenter.setView(this);
	}

	private void handleNavigationItemIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		NavigationItem navItem = (NavigationItem) intent.getSerializableExtra(EXTRAS_NAV_ITEM);
		if (navItem == null) {
			return;
		}

		navItem.getNavigator().navigate(this);
	}

	private void initNavigationItems() {
		viewNavigation.setNavigationItemSelectedListener(this);

		Menu navMenu = viewNavigation.getMenu();
		List<NavigationItem> navItems = mainViewPresenter.getNavigationItems();
		if (navItems != null) {
			for (NavigationItem item : navItems) {
				MenuItem menuItem = navMenu.add(Menu.NONE, item.getId(), Menu.NONE, item.getTitle());
				Intent intent = new Intent(this, MainActivityImpl.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(EXTRAS_NAV_ITEM, item);
				menuItem.setIntent(intent);

				if (item.isDefault()) {
					viewNavigation.setCheckedItem(item.getId());
				}
			}
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		drawerLayout.closeDrawer(GravityCompat.START);

		return false;
	}

	protected void initMainViews() {
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	@Override
	public void startActionUpdate() {
		UpdateService.startActionUpdate(this);
	}

	/***
	 * event bus
	 ***/

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
	}

	/***
	 * end - event bus
	 ***/
}
