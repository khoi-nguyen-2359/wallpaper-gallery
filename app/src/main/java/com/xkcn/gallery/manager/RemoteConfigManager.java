package com.xkcn.gallery.manager;

import com.xkcn.gallery.model.NavigationItem;

import java.util.List;

/**
 * Created by khoinguyen on 9/10/16.
 */

public interface RemoteConfigManager {
	void setupDefaultConfigs();

	List<NavigationItem> getNavigationMenuItems();
}
