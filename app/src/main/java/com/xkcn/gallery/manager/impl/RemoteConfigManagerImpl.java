package com.xkcn.gallery.manager.impl;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xkcn.gallery.R;
import com.xkcn.gallery.manager.RemoteConfigManager;
import com.xkcn.gallery.model.NavigationItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 9/10/16.
 */
public class RemoteConfigManagerImpl implements RemoteConfigManager {
	private static final String KEY_NAV_MENU_ITEMS = "nav_menu_items";
	private static final long FIREBASE_CACHE_PERIOD = 0;

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
		try {
			String jsonItemArray = firebaseConfig.getString(KEY_NAV_MENU_ITEMS);
			Type type = new TypeToken<List<NavigationItem>>() {
			}.getType();
			return gson.fromJson(jsonItemArray, type);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ArrayList<>();
	}

	@Override
	public Observable<Boolean> fetchRemoteConfig() {
		return Observable.create(new Observable.OnSubscribe<Boolean>() {
			@Override
			public void call(final Subscriber<? super Boolean> subscriber) {
				Task<Void> taskFetchRemoteConfigs = firebaseConfig.fetch(FIREBASE_CACHE_PERIOD);
				taskFetchRemoteConfigs.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						subscriber.onNext(true);
					}
				});

				taskFetchRemoteConfigs.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						subscriber.onError(e);
					}
				});

				taskFetchRemoteConfigs.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						firebaseConfig.activateFetched();
						subscriber.onCompleted();
					}
				});
			}
		});
	}
}
