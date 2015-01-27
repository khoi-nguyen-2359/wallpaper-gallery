package com.xkcn.crawler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.util.UiUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

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

    @InjectView(R.id.iv_photo)    ImageViewTouch ivPhoto;

    private View viewDecor;
    private Photo photo;
    private GestureDetector toggleStatusBarDetector;
    
    private Handler hideSystemUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UiUtils.hideStatusBar(null, viewDecor);
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

    private void initViews() {
        ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Picasso.with(this).load(photo.getPhotoHigh()).into(ivPhoto);

        viewDecor = getWindow().getDecorView();
        UiUtils.makeStableLayout(viewDecor);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        toggleStatusBarDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void initData() {
        photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
        toggleStatusBarDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean visible = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || UiUtils.isStatusBarVisible(null, viewDecor);    // wont apply this toggling for pre-jellybean
                if (visible) {
                    UiUtils.hideStatusBar(null, viewDecor);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    UiUtils.showStatusBar(null, viewDecor);
                } else {
                    return false;
                }
                return true;
            }
        });
    }
}
