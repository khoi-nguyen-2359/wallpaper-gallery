package com.xkcn.crawler;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.squareup.picasso.Picasso;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.presenter.PhotoActionsPresenter;
import com.xkcn.crawler.util.U;
import com.xkcn.crawler.util.UiUtils;
import com.xkcn.crawler.view.PhotoActionsTextView;

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
    private PhotoActionsTextView viewPhotoActions;
    private View viewContent;

    public static Intent intentViewSinglePhoto(Context context, Photo photo) {
        Intent i = new Intent(context, SinglePhotoActivity.class);
        i.putExtra(EXTRA_PHOTO, photo);

        return i;
    }

    @InjectView(R.id.iv_photo)    ImageViewTouch ivPhoto;

    private View viewDecor;
    private Photo photo;
    private GestureDetector toggleStatusBarDetector;
    private PhotoActionsPresenter photoActionsPresenter;
    private ViewPropertyAnimator animHidePhotoActions;
    private ViewPropertyAnimator animShowPhotoActions;

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
        viewContent = findViewById(R.id.content_view);

        ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Picasso.with(this).load(photo.getPhotoHigh()).into(ivPhoto);

        viewPhotoActions = (PhotoActionsTextView) findViewById(R.id.photo_actions);
        viewPhotoActions.setPresenter(photoActionsPresenter);

        viewDecor = getWindow().getDecorView();
        viewDecor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (UiUtils.isStatusBarVisible(visibility)) {
                    U.dd("visible %d", visibility);
                    viewPhotoActions.setVisibility(View.VISIBLE);
                    viewPhotoActions.animate().setListener(null).y(viewContent.getHeight()-getResources().getDimension(R.dimen.abc_action_bar_default_height_material)).start();
                } else {
                    U.dd("invisible");
                    viewPhotoActions.animate().y(viewContent.getHeight()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            viewPhotoActions.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
            }
        });

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
                boolean visible = UiUtils.isStatusBarVisible(null, viewDecor);
                if (visible) {
                    UiUtils.hideStatusBar(null, viewDecor);
                } else {
                    UiUtils.showStatusBar(null, viewDecor);
                }

                return true;
            }
        });

        photoActionsPresenter = new PhotoActionsPresenter();
        photoActionsPresenter.setPhoto(photo);
    }
}
