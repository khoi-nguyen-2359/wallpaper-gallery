package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xkcn.crawler.R;
import com.xkcn.crawler.event.UpdateFinishedEvent;
import com.xkcn.crawler.util.P;
import com.xkcn.crawler.util.U;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.HashSet;

/**
 * Created by khoinguyen on 2/21/15.
 */
public class SidebarView extends FrameLayout implements View.OnClickListener {
    private static final String FORMAT_LATEST_UPDATE = "hh:mma dd/MM/YYYY";

    private Button tvCatHotest;
    private Button tvCatLatest;
//    private SelectableTagCloud tagCloud;
    private Button[] arrItems;
    private long currCheckedId;
    private CompoundButton.OnCheckedChangeListener onTagItemCheckChanged;
    private OnClickListener onItemActivated;
    private TextView tvLastUpdateTime;

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
        tvLastUpdateTime = (TextView) findViewById(R.id.tv_last_update_time);
        updateLatestUpdateText();
//        tagCloud = (SelectableTagCloud) findViewById(R.id.tag_cloud);
//        tagCloud.setOnItemCheckChanged(onTagItemCheckChanged);
//        tagCloud.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tagCloud.setItemsEnabled(true);
//            }
//        });

//        tagCloud.setTags(Arrays.asList("1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555","1", "22", "333", "4444", "55555", "111", "22", "333", "4444", "55555"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View layoutSidebarItems = findViewById(R.id.layout_sidebar_items);
            int paddingTop = U.getStatusBarHeight(getResources()) + layoutSidebarItems.getPaddingTop();
            int paddingBottom = U.getNavigationBarHeight(getResources()) + layoutSidebarItems.getPaddingBottom();
            layoutSidebarItems.setPadding(layoutSidebarItems.getPaddingLeft(), paddingTop, layoutSidebarItems.getPaddingRight(), paddingBottom);
        }

        tvCatLatest.setOnClickListener(this);
        tvCatHotest.setOnClickListener(this);

        arrItems = new Button[] {tvCatHotest, tvCatLatest};
    }

    public void setOnSidebarItemActivated(OnClickListener onItemClick) {
        this.onItemActivated = onItemClick;
    }

//    public void setTags(HashSet<String> strings) {
//        tagCloud.setTags(strings);
//        tagCloud.populateTagViews();
//    }

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

    private OnClickListener onTagItemClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onClick(View v) {
        if (activateItem(v.getId())) {
//            tagCloud.setItemsEnabled(false);

            if (onItemActivated != null) {
                onItemActivated.onClick(v);
            }
        }
    }

    public void onEvent(UpdateFinishedEvent event) {
        updateLatestUpdateText();
    }

    private void updateLatestUpdateText() {
        long lastUpdateTime = P.getLastUpdateTime();
        DateTime lastUpdateDateTime = new DateTime(lastUpdateTime);
        tvLastUpdateTime.setText(String.format("%s\n%s", getResources().getString(R.string.sidebar_latest_update_at), lastUpdateDateTime.toString(FORMAT_LATEST_UPDATE)));
    }
}
