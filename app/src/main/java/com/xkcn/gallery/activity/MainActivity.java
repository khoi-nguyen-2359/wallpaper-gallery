package com.xkcn.gallery.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.khoinguyen.ui.UiUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListingPagerAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.OnPhotoListItemClicked;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.event.SetWallpaperClicked;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.util.AndroidUtils;
import com.xkcn.gallery.view.MainView;
import com.xkcn.gallery.view.PhotoDetailsViewPager;
import com.xkcn.gallery.view.PhotoListingView;
import com.xkcn.gallery.view.PhotoListingViewPagerImpl;
import com.xkcn.gallery.view.custom.ClippingRevealDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView {

    @Bind(R.id.pager_photo_listing) PhotoListingViewPagerImpl pagerPhotoListing;
    @Bind(R.id.nav_view) NavigationView viewNavigation;
    @Bind(R.id.main_coordinator_layout) CoordinatorLayout mainCoordinatorLayout;
    @Bind(R.id.app_bar) AppBarLayout appBarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_container) FrameLayout toolbarContainerLayout;
    @Bind(R.id.drawee_transit) ClippingRevealDraweeView transitDraweeView;
    @Bind(R.id.drawee_transit_backdrop) View transitBackdrop;
    @Bind(R.id.pager_photo_details) PhotoDetailsViewPager pagerPhotoDetails;

    protected MainViewPresenter presenter;
    protected PhotoListingViewPagerPresenter listingPagerPresenter;
    protected PhotoListingViewPresenter detailsPagerPresenter;
    private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
    protected Dialog proDlg;

    private PhotoViewRevealInfo photoViewRevealInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initTemplateViews();
        initViews();
        listingPagerPresenter.loadPageCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkToCrawlPhoto();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        listingPagerPresenter.saveLastWatchedPhotoListPage();
    }

    private void initViews() {
        transitDraweeView.getHierarchy().setFadeDuration(0);

        pagerPhotoListing.setPresenter(listingPagerPresenter);
        pagerPhotoListing.addOnPageChangeListener(onPhotoListPageChanged);
        pagerPhotoListing.setPhotoComponent(getPhotoComponent());
        listingPagerPresenter.setView(pagerPhotoListing);

        pagerPhotoDetails.setPresenter(detailsPagerPresenter);
        detailsPagerPresenter.setView(pagerPhotoDetails);
        pagerPhotoDetails.setDraggingListener(detailsPagerDraggingListener);
        pagerPhotoDetails.addOnPageChangeListener(onPhotoDetailsPageChanged);
    }

    private void initData() {
        photoViewRevealInfo = new PhotoViewRevealInfo();
        presenter = new MainViewPresenter(photoDownloader, this, preferenceRepository);

        SystemBarTintManager kitkatTintManager = new SystemBarTintManager(this);
        kitkatSystemBarConfig = kitkatTintManager.getConfig();

        listingPagerPresenter = new PhotoListingViewPagerPresenter();
        getPhotoComponent().inject(listingPagerPresenter);
        getPreferencesComponent().inject(listingPagerPresenter);
        listingPagerPresenter.setView(pagerPhotoListing);

        detailsPagerPresenter = new PhotoListingViewPresenter(preferenceRepository.getListPagerPhotoPerPage());
        getPhotoComponent().inject(detailsPagerPresenter);
    }

    protected void initTemplateViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        viewNavigation.setNavigationItemSelectedListener(this);

        applyWindowInsets();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(pagerPhotoListing, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, final WindowInsetsCompat insets) {
                toolbarContainerLayout.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                pagerPhotoListing.setPadding(0,0,0,insets.getSystemWindowInsetBottom());

                return insets;
            }
        });

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            toolbarContainerLayout.setPadding(0, kitkatSystemBarConfig.getPixelInsetTop(false), 0, 0);
            pagerPhotoListing.setPadding(0,0,0,kitkatSystemBarConfig.getPixelInsetBottom());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (pagerPhotoDetails.getVisibility() == View.VISIBLE) {
            pagerPhotoDetails.setVisibility(View.GONE);
            transitBackdrop.setAlpha(0);
            transitDraweeView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hotest) {
            listingPagerPresenter.setCurrentType(PhotoListingPagerAdapter.TYPE_HOTEST);
        } else if (id == R.id.nav_latest) {
            listingPagerPresenter.setCurrentType(PhotoListingPagerAdapter.TYPE_LATEST);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void startActionUpdate() {
        UpdateService.startActionUpdate(this);
    }

    private ViewPager.OnPageChangeListener onPhotoListPageChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0) {
                detailsPagerPresenter.loadPhotoListPage(position + 1, listingPagerPresenter.getCurrentType());
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /***
     * event bus
     ***/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
        listingPagerPresenter.loadPageCount();
    }

    @Subscribe
    public void handleOnPhotoItemClicked(final OnPhotoListItemClicked event) {
        appBarLayout.setExpanded(false, false);

        photoViewRevealInfo.itemPosition = event.getItemPosition();
        photoViewRevealInfo.startRect = event.getStartRect();
        RectF endRect = new RectF(0, 0, mainCoordinatorLayout.getWidth(), mainCoordinatorLayout.getHeight());

        transitDraweeView.setVisibility(View.VISIBLE);
        transitDraweeView.setImageUri(event.getPhotoDetails().getLowResUri());

        transitDraweeView.createExpanseAnimation(photoViewRevealInfo.startRect, endRect)
                .addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pagerPhotoDetails.setCurrentItem(photoViewRevealInfo.itemPosition, false);
                        pagerPhotoDetails.setTranslationX(0);
                        pagerPhotoDetails.setTranslationY(0);
                        pagerPhotoDetails.setVisibility(View.VISIBLE);
                        transitDraweeView.setVisibility(View.GONE);
                        photoViewRevealInfo.isExpanded = true;
                    }
                })
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        transitBackdrop.setAlpha(animation.getAnimatedFraction());
                    }
                })
                .run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SetWallpaperClicked event) {
        PhotoDetails photoDetails = event.getPhoto();
        presenter.loadWallpaperSetting(photoDetails);
    }

    /*** end - event bus ***/

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        proDlg = ProgressDialog.show(this, null, getString(R.string.msg_wait_a_moment), true);
    }

    @Override
    public void hideLoading() {
        UiUtils.dismissDlg(proDlg);
    }

    @Override
    public void showWallpaperChooser(File photoFile) {
        Uri uri = Uri.fromFile(photoFile);
        AndroidUtils.startSetWallpaperChooser(this, uri);
    }

    private PhotoDetailsViewPager.DraggingListener detailsPagerDraggingListener = new PhotoDetailsViewPager.DraggingListener() {
        @Override
        public void onStartDragging(PhotoDetailsViewPager detailsPager) {
            transitBackdrop.setAlpha(0.75f);
        }

        @Override
        public void onEndDragging(PhotoDetailsViewPager detailsPager) {
            PhotoListingView currentPageView = pagerPhotoListing.getCurrentPageView();
            View itemView = currentPageView.getPhotoItemView(pagerPhotoDetails.getCurrentItem());
            int[] location = new int[2];
            itemView.getLocationInWindow(location);
            RectF endRect = new RectF(location[0], location[1], location[0] + itemView.getWidth(), location[1] + itemView.getHeight());

            RectF startRect = new RectF(pagerPhotoDetails.getX(), pagerPhotoDetails.getY(), pagerPhotoDetails.getX() + pagerPhotoDetails.getWidth(), pagerPhotoDetails.getY() + pagerPhotoDetails.getHeight());
            transitDraweeView.createShrinkAnimation(startRect, endRect)
                    .addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            transitDraweeView.setVisibility(View.VISIBLE);
                            pagerPhotoDetails.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            transitDraweeView.setVisibility(View.GONE);
                            photoViewRevealInfo.isExpanded = false;
                        }
                    })
                    .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            transitBackdrop.setAlpha(1 - animation.getAnimatedFraction());
                        }
                    })
                    .run();
        }
    };

    private ViewPager.OnPageChangeListener onPhotoDetailsPageChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0) {
                setCurrentListingItemByCurrentDetailsItem();
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setCurrentListingItemByCurrentDetailsItem() {
        if (!photoViewRevealInfo.isExpanded) {
            return;
        }

        int currentDetailsItem = pagerPhotoDetails.getCurrentItem();
        PhotoListingView currentPageView = pagerPhotoListing.getCurrentPageView();
        currentPageView.displayPhotoItem(currentDetailsItem);

        PhotoDetails photoDetails = detailsPagerPresenter.getCachedPhotoDetails(currentDetailsItem);
        transitDraweeView.setImageUri(photoDetails.getLowResUri());
    }

    private static class PhotoViewRevealInfo {
        public boolean isExpanded = false;
        public int itemPosition;
        public RectF startRect;
    }
}
