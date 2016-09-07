package com.xkcn.gallery.event;

/**
 * Created by khoinguyen on 1/19/15.
 */
public class CrawlNextPageEvent {
	private int page;

	public CrawlNextPageEvent(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
