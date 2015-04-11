package com.xkcn.crawler;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;
import com.xkcn.crawler.imageloader.XkcnImageLoader;
import com.xkcn.crawler.imageloader.XkcnImageLoaderFactory;
import com.xkcn.crawler.photomanager.PhotoDownloadManager;
import com.xkcn.crawler.photomanager.StorageUtils;
import com.xkcn.crawler.util.UiUtils;
import com.xkcn.crawler.view.PhotoActionsView;

import java.io.File;

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
    private PhotoActionsView viewPhotoActions;
    private View viewContent;
    private boolean enabledToggleStatusBar;
    private PhotoDownloadManager photoDownloadManager;
    private XkcnImageLoader xkcnImageLoader;

    public static Intent intentViewSinglePhoto(Context context, Photo photo) {
        Intent i = new Intent(context, SinglePhotoActivity.class);
        i.putExtra(EXTRA_PHOTO, photo);

        return i;
    }

    @InjectView(R.id.iv_photo)      ImageViewTouch ivPhoto;
    @InjectView(R.id.progress_bar)  ProgressBar progressBar;

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
        loadPhoto();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XkcnFrescoImageLoader.release(this, ivPhoto);
    }

    private void delayedHide(long milis) {
        hideSystemUIHandler.removeMessages(0);
        hideSystemUIHandler.sendEmptyMessageDelayed(0, milis);
    }

    private XkcnImageLoader.Callback loadSingleHighPhotoCallback = new XkcnImageLoader.Callback() {
        @Override
        public void onLoaded(Bitmap bitmap) {
        }

        @Override
        public void onFailed() {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SinglePhotoActivity.this, R.string.photo_action_download_failed_retry, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCompleted() {
            progressBar.setVisibility(View.GONE);
            enabledToggleStatusBar = true;
            UiUtils.showStatusBar(getWindow(), viewDecor);

            photoDownloadManager.asyncDownload(photo.getIdentifier(), photo.getPhotoHigh());
        }
    };

    private void loadPhoto() {
        File downloadedPhoto = StorageUtils.getDownloadedPhotoFile(photo.getPhotoHigh());
        if (downloadedPhoto.exists()) {
            xkcnImageLoader.load(downloadedPhoto, ivPhoto, loadSingleHighPhotoCallback);
        } else {
            xkcnImageLoader.load(photo.getPhotoHigh(), ivPhoto, loadSingleHighPhotoCallback);
        }
    }

    private void initViews() {
        viewContent = findViewById(R.id.content_view);
        viewDecor = getWindow().getDecorView();

        ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        viewPhotoActions = (PhotoActionsView) findViewById(R.id.photo_actions);
        viewPhotoActions.bind(photo);

        viewDecor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (UiUtils.isStatusBarVisible(visibility)) {
                    viewPhotoActions.setVisibility(View.VISIBLE);
                    viewPhotoActions.animate().setListener(null).y(viewContent.getHeight()-getResources().getDimension(R.dimen.abc_action_bar_default_height_material)).start();
                } else {
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
        enabledToggleStatusBar = false;
        toggleStatusBarDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!enabledToggleStatusBar)   return false;

                boolean visible = UiUtils.isStatusBarVisible(null, viewDecor);
                if (visible) {
                    UiUtils.hideStatusBar(null, viewDecor);
                    hideSystemUIHandler.removeMessages(0);
                } else {
                    UiUtils.showStatusBar(null, viewDecor);
                }

                return true;
            }
        });

        photoDownloadManager = PhotoDownloadManager.getInstance();
        xkcnImageLoader = XkcnImageLoaderFactory.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
