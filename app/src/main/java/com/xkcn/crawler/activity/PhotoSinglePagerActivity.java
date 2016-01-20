package com.xkcn.crawler.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xkcn.crawler.R;
import com.xkcn.crawler.adapter.PhotoListPagerAdapter;
import com.xkcn.crawler.adapter.PhotoSinglePagerAdapter;
import com.xkcn.crawler.data.PhotoDetailsSqliteDataStore;
import com.xkcn.crawler.data.PreferenceDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.presenter.PhotoSinglePagerViewPresenter;
import com.xkcn.crawler.usecase.PhotoListingUsecase;
import com.xkcn.crawler.util.UiUtils;
import com.xkcn.crawler.view.PhotoActionsView;
import com.xkcn.crawler.view.PhotoSinglePagerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by khoinguyen on 1/21/15.
 */
public abstract class PhotoSinglePagerActivity extends PhotoPagerActivity implements PhotoSinglePagerView {
    public static final String EXTRA_PHOTO_LIST_PAGE = "EXTRA_PHOTO_LIST_PAGE";
    private static final String EXTRA_SELECTED_POSITION = "EXTRA_SELECTED_POSITION";
    private static final String EXTRA_LISTING_TYPE = "EXTRA_LISTING_TYPE";
    public static final long PERIOD_HIDE_SYSTEMUI = 3000;

    private boolean enabledToggleStatusBar;

    public static Intent intentViewSinglePhoto(Context context, int listingType, int page, int selectedPosition) {
        Intent i = new Intent(context, PhotoSinglePagerActivityImpl.class);
        i.putExtra(EXTRA_PHOTO_LIST_PAGE, page);
        i.putExtra(EXTRA_SELECTED_POSITION, selectedPosition);
        i.putExtra(EXTRA_LISTING_TYPE, listingType);

        return i;
    }

    @Bind(R.id.pager_photo_single) ViewPager pagerPhotoSingle;
    @Bind(R.id.photo_actions) PhotoActionsView viewPhotoActions;
    @Bind(android.R.id.content) View viewBodyContent;

    private PhotoSinglePagerViewPresenter presenter;
    private GestureDetector toggleStatusBarDetector;
    private PhotoSinglePagerAdapter adapterPhotoSingles;

    private View viewDecor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_single_pager);
        ButterKnife.bind(this);
        initData();
        initViews();

        loadPhotoList();
    }

    private void initViews() {
        viewDecor = getWindow().getDecorView();
        viewDecor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (UiUtils.isStatusBarVisible(visibility)) {
                    viewPhotoActions.setVisibility(View.VISIBLE);
                    viewPhotoActions.animate().setListener(null).y(viewBodyContent.getHeight() - getResources().getDimension(R.dimen.abc_action_bar_default_height_material)).start();
                } else {
                    viewPhotoActions.animate().y(viewBodyContent.getHeight()).setListener(new Animator.AnimatorListener() {
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUiDelayed(PERIOD_HIDE_SYSTEMUI);
        } else {
            hideSystemUIHandler.removeMessages(0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        toggleStatusBarDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void hideSystemUiDelayed(long milis) {
        hideSystemUIHandler.removeMessages(0);
        hideSystemUIHandler.sendEmptyMessageDelayed(0, milis);
    }

    private Handler hideSystemUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UiUtils.hideStatusBar(null, viewDecor);
        }
    };

    private void loadPhotoList() {
        presenter.createPhotoQueryObservable().subscribe(new Subscriber<List<PhotoDetails>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<PhotoDetails> photoDetailses) {
                initPager(photoDetailses, getSelectedPosition());
            }
        });
    }

    private void initData() {
        int page = getIntent().getIntExtra(EXTRA_PHOTO_LIST_PAGE, 0);
        int listingType = getCurrentType();

        PreferenceDataStore prefDataStore = new PreferenceDataStoreImpl();
        int perPage = prefDataStore.getListPagerPhotoPerPage();
        PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(new PhotoDetailsSqliteDataStore(), perPage);
        presenter = new PhotoSinglePagerViewPresenter(photoListingUsecase, listingType, page);

        enabledToggleStatusBar = false;
        toggleStatusBarDetector = new StatusBarToggler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initPager(List<PhotoDetails> photoListPage, int selectedPosition) {
        adapterPhotoSingles = new PhotoSinglePagerAdapter(getSupportFragmentManager(), photoListPage);
        pagerPhotoSingle.setAdapter(adapterPhotoSingles);
        pagerPhotoSingle.setCurrentItem(selectedPosition);
    }

    public int getSelectedPosition() {
        return getIntent().getIntExtra(EXTRA_SELECTED_POSITION, 0);
    }

    @Override
    public int getCurrentType() {
        Intent intent = getIntent();
        return intent != null ? intent.getIntExtra(EXTRA_LISTING_TYPE, 0) : PhotoListPagerAdapter.TYPE_INVALID;
    }

    class StatusBarToggler extends GestureDetector {
        public StatusBarToggler(Context context) {
            super(context, new SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    if (!enabledToggleStatusBar)   return false;

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
        }
    }

}
