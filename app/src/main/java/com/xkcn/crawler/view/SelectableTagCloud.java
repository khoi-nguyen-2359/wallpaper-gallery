package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.xkcn.crawler.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by khoinguyen on 2/26/15.
 */
public class SelectableTagCloud extends LinearLayout implements CompoundButton.OnCheckedChangeListener {
    private HashSet<String> tags;
    private CompoundButton.OnCheckedChangeListener onItemCheckChanged;
    private List<CheckBox> arrTagViewItems;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectableTagCloud(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SelectableTagCloud(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SelectableTagCloud(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableTagCloud(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
//        setOnClickListener(onStartClicked);
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public void populateTagViews() {
        if (this.tags == null || this.tags.size() == 0)
            return;

        removeAllViews();
        arrTagViewItems = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int rowWidth = 0;
        int maxWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        LinearLayout row = addRow();
        for (String tag : this.tags) {
            CheckBox tagView = (CheckBox) inflater.inflate(R.layout.item_tag_view, row, false);
            tagView.setOnCheckedChangeListener(this);
            LinearLayout.LayoutParams lp = (LayoutParams) tagView.getLayoutParams();
            tagView.setText(tag);
            float itemWidth = tagView.getPaint().measureText(tag) + tagView.getPaddingLeft() + tagView.getPaddingRight() + lp.leftMargin + lp.rightMargin;
            rowWidth += itemWidth;

            if (maxWidth <= rowWidth) {
                row = addRow();
                rowWidth = (int) itemWidth;
            }

            row.addView(tagView);
            arrTagViewItems.add(tagView);
        }
    }

    private LinearLayout addRow() {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER);
        row.setOrientation(HORIZONTAL);
        addView(row);

        return row;
    }

    public void setOnItemCheckChanged(CompoundButton.OnCheckedChangeListener onItemCheckChanged) {
        this.onItemCheckChanged = onItemCheckChanged;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (onItemCheckChanged != null) {
            onItemCheckChanged.onCheckedChanged(buttonView, isChecked);
        }
    }

    public void setItemsEnabled(boolean val) {
        if (arrTagViewItems == null) {
            return;
        }

        for (CheckBox cb : arrTagViewItems) {
            cb.setEnabled(val);
        }
    }

//    private OnClickListener onStartClicked = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            setItemsEnabled(true);
//        }
//    };
}
