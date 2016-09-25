package com.xkcn.gallery.data.cloud.model;

import java.io.Serializable;

/**
 * Created by khoinguyen on 9/26/16.
 */

public class PhotoCollection implements Serializable {
	private String name;
	private String query;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
