package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xkcn.crawler.R;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.presenter.PhotoActionsPresenter;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionsView extends FrameLayout {
    private PhotoActionsPresenter presenter = new PhotoActionsPresenter();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoActionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PhotoActionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhotoActionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoActionsView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_photo_actions, this, true);

        findViewById(R.id.bt_download).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDownloadClicked();
            }
        });

        findViewById(R.id.bt_set_wallpaper).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSetWallpaperClicked();
            }
        });

        findViewById(R.id.bt_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onShareClicked();
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
