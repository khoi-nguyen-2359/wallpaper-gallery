package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xkcn.crawler.R;
import com.xkcn.crawler.presenter.PhotoActionsPresenter;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionsTextView extends FrameLayout {
    private PhotoActionsPresenter presenter = new PhotoActionsPresenter();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoActionsTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PhotoActionsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhotoActionsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoActionsTextView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_photo_actions_text, this, true);

        findViewById(R.id.tv_set_wallpaper).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSetWallpaperClicked((android.app.Activity) getContext());
            }
        });

        findViewById(R.id.tv_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onShareClicked(getContext());
            }
        });
    }

    public PhotoActionsPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PhotoActionsPresenter presenter) {
        this.presenter = presenter;
    }
}
