package com.xkcn.gallery.manager.impl;

import android.content.res.Resources;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xkcn.gallery.R;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.model.NavigationItem;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khoinguyen on 9/10/16.
 */
public class RemoteConfigManagerImpl implements RemoteConfigManager {
	private static final String KEY_NAV_MENU_ITEMS = "nav_menu_items";

	private Resources resources;
	private FirebaseRemoteConfig firebaseConfig;
	private Gson gson;

	public RemoteConfigManagerImpl(Resources resources, FirebaseRemoteConfig firebaseConfig, Gson gson) {
		this.resources = resources;
		this.firebaseConfig = firebaseConfig;
		this.gson = gson;
	}

	@Override
	public void setupDefaultConfigs() {
		Map<String, Object> defaultRemoteConfigs = new HashMap<>();
		defaultRemoteConfigs.put(KEY_NAV_MENU_ITEMS, resources.getString(R.string.remote_configs_nav_menu_items));

		firebaseConfig.setDefaults(defaultRemoteConfigs);
	}

	@Override
	public List<NavigationItem> getNavigationMenuItems() {
		String jsonItemArray = firebaseConfig.getString(KEY_NAV_MENU_ITEMS);
		Type type = new TypeToken<List<NavigationItem>>() {
		}.getType();
		return gson.fromJson(jsonItemArray, type);
//		JsonArray menuItemArray = gson.fromJson(jsonItemArray, JsonArray.class);
//		if (menuItemArray != null && !menuItemArray.isJsonNull() && menuItemArray.isJsonArray()) {
//			for (JsonElement element : menuItemArray) {
//				if (element == null || element.isJsonNull() || !element.isJsonObject()) {
//					continue;
//				}
//
//				JsonObject elementObj = element.getAsJsonObject();
//				String type = elementObj.getAsJsonPrimitive(DataContracts.NAVIGATION_ITEM_FIELD_TYPE).getAsString();
//				NavigationItem navItem = null;
//				if (DataContracts.NAVIGATION_ITEM_TYPE_COLLECTION.equalsIgnoreCase(type)) {
//					navItem = new CollectionNavigationItem();
//					navItems.add(navItem);
//
//					navItem.setId(elementObj.getAsJsonPrimitive(DataContracts.NAVIGATION_ITEM_FIELD_ID).getAsInt());
//					navItem.setTitle(elementObj.getAsJsonPrimitive(DataContracts.NAVIGATION_ITEM_FIELD_TITLE).getAsString());
//					navItem.setData(elementObj.getAsJsonPrimitive(DataContracts.NAVIGATION_ITEM_FIELD_DATA).getAsString());
//				}
//
//				if (navItem != null) {
//					navItem.setType(type);
//				}
//			}
//		}

//		return navItems;
	}
}
