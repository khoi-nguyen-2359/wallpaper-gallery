package com.xkcn.gallery.activity;

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

import com.khoinguyen.logging.L;
import com.khoinguyen.ui.UiUtils;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListPagerAdapter;
import com.xkcn.gallery.adapter.PhotoSinglePagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PagerSinglePhotoSelected;
import com.xkcn.gallery.presenter.PhotoSinglePagerViewPresenter;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoActionsView;
import com.xkcn.gallery.view.PhotoSinglePagerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by khoinguyen on 1/21/15.
 */
public abstract class PhotoSinglePagerActivity extends PhotoPagerActivity implements PhotoSinglePagerView {
    public static final String EXTRA_PHOTO_LIST_PAGE = "EXTRA_PHOTO_LIST_PAGE";
    private static final String EXTRA_SELECTED_POSITION = "EXTRA_SELECTED_POSITION";
    private static final String EXTRA_LISTING_TYPE = "EXTRA_LISTING_TYPE";
    public static final long PERIOD_HIDE_SYSTEMUI = 3000;

    private boolean enabledToggleStatusBar;
    private L logger;
    private PhotoPagerLoadingTracker photoPagerLoadingTracker;

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

        presenter.loadPhotoListPage();
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

    private void initData() {
        int page = getIntent().getIntExtra(EXTRA_PHOTO_LIST_PAGE, 0);
        int listingType = getCurrentType();

        int perPage = preferenceRepository.getListPagerPhotoPerPage();
        PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(photoDetailsRepository);
        presenter = new PhotoSinglePagerViewPresenter(photoListingUsecase, listingType, page, perPage);
        presenter.setView(this);

        enabledToggleStatusBar = false;
        toggleStatusBarDetector = new StatusBarToggler(this);

        logger = L.get(getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ViewPager.OnPageChangeListener onPageChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0) {
                bindPhotoToActionView(position);
            }
        }

        @Override
        public void onPageSelected(int position) {
            photoPagerLoadingTracker.changeCurrentPhotoPage(getPhotoDetails(position).getIdentifier());
            EventBus.getDefault().post(new PagerSinglePhotoSelected());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private PhotoDetails getPhotoDetails(int position) {
        if (adapterPhotoSingles == null) {
            return null;
        }

        List<PhotoDetails> photos = adapterPhotoSingles.getPhotoListPage();
        if (photos == null || position < 0 || position >= photos.size()) {
            return null;
        }

        return photos.get(position);
    }

    private void bindPhotoToActionView(int position) {
        viewPhotoActions.bind(getPhotoDetails(position));
    }

    @Override
    public void setupPagerAdapter(List<PhotoDetails> photoListPage) {
        if (adapterPhotoSingles == null) {
            photoPagerLoadingTracker = new PhotoPagerLoadingTracker();
            adapterPhotoSingles = new PhotoSinglePagerAdapter(getSupportFragmentManager());
            pagerPhotoSingle.addOnPageChangeListener(onPageChanged);
            pagerPhotoSingle.setAdapter(adapterPhotoSingles);
        }

        int selectedPosition = getSelectedPosition();

        adapterPhotoSingles.setPhotoDatas(photoListPage);
        photoPagerLoadingTracker.setup(photoListPage, selectedPosition);
        adapterPhotoSingles.notifyDataSetChanged();
        pagerPhotoSingle.setCurrentItem(selectedPosition);
        logger.d("setupPagerAdapter %d, select %d", photoListPage != null ? photoListPage.size() : 0, selectedPosition);
    }

    @Override
    public PhotoPagerLoadingTracker getPhotoPagerLoadingTracker() {
        return photoPagerLoadingTracker;
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
