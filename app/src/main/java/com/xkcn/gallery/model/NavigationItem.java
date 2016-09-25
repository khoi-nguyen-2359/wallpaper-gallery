package com.xkcn.gallery.model;

import com.xkcn.gallery.view.navigator.Navigator;

import java.io.Serializable;

/**
 * Created by khoinguyen on 9/9/16.
 */
public class NavigationItem implements Serializable {
	private int id;
	private String type;
	private String title;
	private String data;
	private boolean isDefault;
	private Navigator navigator;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean aDefault) {
		isDefault = aDefault;
	}

	public Navigator getNavigator() {
		return navigator;
	}

	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}
}
