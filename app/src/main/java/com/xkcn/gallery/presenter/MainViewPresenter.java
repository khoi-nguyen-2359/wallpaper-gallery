package com.xkcn.gallery.presenter;

import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.model.NavigationItem;
import com.xkcn.gallery.view.interfaces.MainView;

import java.util.List;

/**
 * Created by khoinguyen on 9/12/16.
 */

public class MainViewPresenter {
	private MainView view;

	private RemoteConfigManager remoteConfigManager;
	private LocalConfigManager localConfigManager;

	public MainViewPresenter(RemoteConfigManager remoteConfigManager, LocalConfigManager localConfigManager) {
		this.remoteConfigManager = remoteConfigManager;
		this.localConfigManager = localConfigManager;
	}

	public void checkToCrawlPhoto() {
		if (localConfigManager.getLastPhotoCrawlTime() < System.currentTimeMillis() - localConfigManager.getUpdatePeriod()) {
			view.startActionUpdate();
		}
	}

	public void setView(MainView view) {
		this.view = view;
	}

	public List<NavigationItem> getNavigationItems() {
		return remoteConfigManager.getNavigationMenuItems();
	}
}
