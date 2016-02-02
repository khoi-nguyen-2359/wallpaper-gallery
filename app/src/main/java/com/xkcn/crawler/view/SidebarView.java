package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xkcn.crawler.R;
import com.xkcn.crawler.util.UiUtils;

/**
 * Created by khoinguyen on 2/21/15.
 */
public class SidebarView extends FrameLayout implements View.OnClickListener {
    private static final String FORMAT_LATEST_UPDATE = "hh:mma dd/MM/YYYY";

    private Button tvCatHotest;
    private Button tvCatLatest;

    private Button[] arrItems;
    private OnClickListener onItemActivated;

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
        tvCatHotest = (Button) findViewById(R.id.tv_hotest);
        tvCatLatest = (Button) findViewById(R.id.tv_latest);
        tvCatLatest.setActivated(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View layoutSidebarItems = findViewById(R.id.layout_sidebar_items);
            int paddingTop = UiUtils.getStatusBarHeight(getResources()) + layoutSidebarItems.getPaddingTop();
            int paddingBottom = UiUtils.getNavigationBarHeight(getResources()) + layoutSidebarItems.getPaddingBottom();
            layoutSidebarItems.setPadding(layoutSidebarItems.getPaddingLeft(), paddingTop, layoutSidebarItems.getPaddingRight(), paddingBottom);
        }

        tvCatLatest.setOnClickListener(this);
        tvCatHotest.setOnClickListener(this);

        arrItems = new Button[] {tvCatHotest, tvCatLatest};
    }

    private boolean activateItem(long idToActive) {
        for (Button c : arrItems) {
            if (idToActive == c.getId()) {
                if (c.isActivated())
                    return false;

                c.setActivated(true);
                continue;
            }

            c.setActivated(false);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (activateItem(v.getId())) {
            if (onItemActivated != null) {
                onItemActivated.onClick(v);
            }
        }
    }
}
