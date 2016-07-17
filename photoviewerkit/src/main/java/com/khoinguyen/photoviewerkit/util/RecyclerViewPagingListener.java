package com.khoinguyen.photoviewerkit.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.Arrays;

/**
 * Created by khoi2359 on 4/21/15.
 */
public abstract class RecyclerViewPagingListener extends RecyclerView.OnScrollListener {
    private int offsetToTrigger = 0;
    private boolean enable = true;

    public RecyclerViewPagingListener(int offsetToTrigger) {
        this.offsetToTrigger = offsetToTrigger;
    }

    public void setEnable(boolean value) {
        this.enable = value;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = findLastVisibleItemPosition(layoutManager);

        if (lastVisibleItem >= totalItemCount - offsetToTrigger) {
            if (enable) {
                onNext(recyclerView);
                enable = false;
            }
        }
    }

    protected int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION;
        }

        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] items = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            if (items == null || items.length == 0) {
                return RecyclerView.NO_POSITION;
            }

            Arrays.sort(items);

            return items[items.length - 1];
        }

        return RecyclerView.NO_POSITION;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public abstract void onNext(RecyclerView recyclerView);
}
