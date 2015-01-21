package com.xkcn.crawler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.util.U;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by khoinguyen on 1/21/15.
 */
public class SinglePhotoActivity extends BaseActivity {
    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";
    public static final long PERIOD_HIDE_SYSTEMUI = 3000;

    public static Intent intentViewSinglePhoto(Context context, Photo photo) {
        Intent i = new Intent(context, SinglePhotoActivity.class);
        i.putExtra(EXTRA_PHOTO, photo);

        return i;
    }

    @InjectView(R.id.iv_photo) ImageView ivPhoto;
    @InjectView(R.id.content_view) View viewContent;

    private View viewDecor;
    private Photo photo;
    private GestureDetector clickDetector;

    private Handler hideSystemUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideSystemUI();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);
        ButterKnife.inject(this);
        initData();
        initViews();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(PERIOD_HIDE_SYSTEMUI);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            delayedHide(PERIOD_HIDE_SYSTEMUI);
        } else {
            hideSystemUIHandler.removeMessages(0);
        }
    }

    private void delayedHide(long milis) {
        hideSystemUIHandler.removeMessages(0);
        hideSystemUIHandler.sendEmptyMessageDelayed(0, milis);
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            U.hideSystemUI(viewDecor);
        }
    }

    private void initViews() {
        Picasso.with(this).load(photo.getPhotoHigh()).into(ivPhoto);

        viewDecor = getWindow().getDecorView();

        clickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                boolean visible = (viewDecor.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                if (visible) {
                    hideSystemUI();
                }

                return true;
            }
        });

        viewContent.setClickable(true);
        viewContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return clickDetector.onTouchEvent(event);
            }
        });
    }

    private void initData() {
        photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
    }
}
