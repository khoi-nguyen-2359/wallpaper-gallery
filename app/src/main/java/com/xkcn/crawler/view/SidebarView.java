package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkcn.crawler.R;

import pl.charmas.android.tagview.TagView;

/**
 * Created by khoinguyen on 2/21/15.
 */
public class SidebarView extends FrameLayout {
    private TextView tvCatHotest;
    private TextView tvCatLatest;
    private TagView tagCloud;

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
        tagCloud = (TagView) findViewById(R.id.tag_cloud);
        tagCloud.setTags(new TagView.Tag[]{
                new TagView.Tag("Tag One", Color.parseColor("#0099cc")),
                new TagView.Tag("Tag Two", Color.parseColor("#9933cc"))
        }, " ");
    }

    public void setOnSidebarItemClick(OnClickListener onItemClick) {
        tvCatLatest.setOnClickListener(onItemClick);
        tvCatHotest.setOnClickListener(onItemClick);
    }
}
