package com.xkcn.gallery.manager.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by khoinguyen on 10/23/16.
 */
@RealmClass
public class LastWatchedPhotoPage implements RealmModel {
	public static String FIELD_COLLECTION_NAME = "collectionName";

	@PrimaryKey
	private String collectionName;
	private int lastWatchedFirstVisibleIndex;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public int getLastWatchedFirstVisibleIndex() {
		return lastWatchedFirstVisibleIndex;
	}

	public void setLastWatchedFirstVisibleIndex(int page) {
		this.lastWatchedFirstVisibleIndex = page;
	}
}

