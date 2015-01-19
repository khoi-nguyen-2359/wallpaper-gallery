package com.xkcn.crawler.event;

/**
 * Created by khoinguyen on 1/19/15.
 */
public class CrawlNextPageEvent {
    public CrawlNextPageEvent(int page) {
        this.page = page;
    }

    private int page;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
