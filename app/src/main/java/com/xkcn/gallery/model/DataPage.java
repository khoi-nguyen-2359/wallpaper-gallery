package com.xkcn.gallery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 6/18/16.
 */
public class DataPage<T> {
	private List<T> data = new ArrayList<>();
	private final int start;

	public DataPage(List<T> items, int start) {
		this.data = items;
		this.start = start;
	}

	public List<T> getData() {
		return data;
	}

	public int getStart() {
		return start;
	}

	public int getNextStart() {
		return start + data.size();
	}

	public boolean isDataEmpty() {
		return data == null || data.isEmpty();
	}
}
