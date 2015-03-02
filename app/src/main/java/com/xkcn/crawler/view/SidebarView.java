package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xkcn.crawler.R;
import com.xkcn.crawler.util.U;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by khoinguyen on 2/21/15.
 */
public class SidebarView extends FrameLayout {
    private TextView tvCatHotest;
    private TextView tvCatLatest;
    private SelectableTagCloud tagCloud;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SidebarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SidebarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SidebarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SidebarView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        tvCatHotest = (TextView) findViewById(R.id.tv_hotest);
        tvCatLatest = (TextView) findViewById(R.id.tv_latest);
        tagCloud = (SelectableTagCloud) findViewById(R.id.tag_cloud);

//        tagCloud.setTags(Arrays.asList("1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View layoutSidebarItems = findViewById(R.id.layout_sidebar_items);
            int paddingTop = U.getStatusBarHeight(getResources()) + layoutSidebarItems.getPaddingTop();
            int paddingBottom = U.getNavigationBarHeight(getResources()) + layoutSidebarItems.getPaddingBottom();
            layoutSidebarItems.setPadding(layoutSidebarItems.getPaddingLeft(), paddingTop, layoutSidebarItems.getPaddingRight(), paddingBottom);
        }
    }

    public void setOnSidebarItemClick(OnClickListener onItemClick) {
        tvCatLatest.setOnClickListener(onItemClick);
        tvCatHotest.setOnClickListener(onItemClick);
    }

    public void setTags(HashSet<String> strings) {
        tagCloud.setTags(strings);
        tagCloud.populateTagViews();
    }
}
