package com.xkcn.gallery.viewmodel;

import android.content.Context;

import com.xkcn.gallery.manager.LocalConfigManager;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.model.NavigationItem;
import com.xkcn.gallery.service.UpdateService;

import java.util.List;

/**
 * Created by khoinguyen on 9/12/16.
 */

public class MainViewModel {
	private RemoteConfigManager remoteConfigManager;
	private LocalConfigManager localConfigManager;

	private Context context;

	public MainViewModel(RemoteConfigManager remoteConfigManager, LocalConfigManager localConfigManager, Context context) {
		this.remoteConfigManager = remoteConfigManager;
		this.localConfigManager = localConfigManager;
		this.context = context;
	}

	public void crawlPhoto() {
		if (localConfigManager.getLastPhotoCrawlTime() < System.currentTimeMillis() - localConfigManager.getUpdatePeriod()) {
			UpdateService.startActionUpdate(context);
		}
	}

	public List<NavigationItem> getNavigationItems() {
		return remoteConfigManager.getNavigationMenuItems();
	}
}
