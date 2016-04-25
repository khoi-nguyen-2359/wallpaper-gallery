package com.xkcn.gallery.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xkcn.gallery.R;
import com.khoinguyen.photokit.data.model.PhotoDetails;
import com.xkcn.gallery.event.SetWallpaperClicked;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionsView extends FrameLayout {
    public static final int TYPE_ICON = 0;
    public static final int TYPE_TEXT = 1;

    private int type;
    private PhotoDetails photo;

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

    public void bind(PhotoDetails photo) {
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
        Resources resources = getResources();
        if (TextUtils.isEmpty(photo.getPermalinkMeta())) {
            sendText = resources.getString(R.string.send_to_trailing_text, photo.getPermalink());
        } else {
            Spanned spanned = Html.fromHtml(photo.getPermalinkMeta());
            sendText = resources.getString(R.string.send_to_trailing_text, spanned.toString() + " " + photo.getPermalink());
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, sendText);
        i.setType("text/plain");

        getContext().startActivity(Intent.createChooser(i, resources.getString(R.string.send_to)));
    }

    private void onSetWallpaperClicked() {
        EventBus.getDefault().post(new SetWallpaperClicked(photo));
    }
}
