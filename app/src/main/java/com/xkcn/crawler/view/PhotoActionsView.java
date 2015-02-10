package com.xkcn.crawler.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xkcn.crawler.R;
import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.event.SetWallpaperClicked;
import com.xkcn.crawler.photoactions.PhotoDownloadManager;
import com.xkcn.crawler.util.U;
import com.xkcn.crawler.util.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionsView extends FrameLayout {
    public static final int TYPE_ICON = 0;
    public static final int TYPE_TEXT = 1;

    private int type;
    private Photo photo;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoActionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public PhotoActionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PhotoActionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PhotoActionsView(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PhotoActionsView);
        type = a.getInt(R.styleable.PhotoActionsView_type, 0);
        a.recycle();
    }

    public void bind(Photo photo) {
        this.photo = photo;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initInflation();
    }

    private void initInflation() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (type == TYPE_ICON) {
            inflater.inflate(R.layout.view_photo_actions_icon, this, true);
        } else if (type == TYPE_TEXT) {
            inflater.inflate(R.layout.view_photo_actions_text, this, true);
        }

        findViewById(R.id.bt_set_wallpaper).setOnClickListener(onClick);
        findViewById(R.id.bt_share).setOnClickListener(onClick);
    }

    public OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_set_wallpaper: {
                    onSetWallpaperClicked();
                    break;
                }

                case R.id.bt_share: {
                    onShareClicked();
                    break;
                }
            }
        }
    };

    private void onShareClicked() {
        String sendText = null;
        if (TextUtils.isEmpty(photo.getPermalinkMeta())) {
            sendText = XkcnApp.app.getString(R.string.send_to_trailing_text, photo.getPermalink());
        } else {
            Spanned spanned = Html.fromHtml(photo.getPermalinkMeta());
            sendText = XkcnApp.app.getString(R.string.send_to_trailing_text, spanned.toString() + " " + photo.getPermalink());;
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, sendText);
        i.setType("text/plain");

        getContext().startActivity(Intent.createChooser(i, XkcnApp.app.getString(R.string.send_to)));
    }

    private void onSetWallpaperClicked() {
        EventBus.getDefault().post(new SetWallpaperClicked(photo));
    }
}
