package com.xkcn.gallery.activity;

import android.graphics.Color;
import android.graphics.PointF;
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
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.khoinguyen.logging.L;
import com.khoinguyen.ui.UiUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListItemAdapter;
import com.xkcn.gallery.adapter.PhotoListPagerAdapter;
import com.xkcn.gallery.anim.ZoomToAnimation;
import com.xkcn.gallery.event.OnPhotoListItemClicked;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.presenter.PhotoListPagerViewPresenter;
import com.xkcn.gallery.service.UpdateService;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoListPagerView;
import com.xkcn.gallery.view.custom.DashLineProgressDrawable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public abstract class PhotoListPagerActivity extends PhotoPagerActivity
        implements NavigationView.OnNavigationItemSelectedListener, PhotoListPagerView {

    protected static final int PHOTO_TYPE_DEFAULT = PhotoListPagerAdapter.TYPE_LATEST;
    private static final int DEF_OFFSCREEN_PAGE = 4;

    protected PhotoListPagerAdapter adapterPhotoPages;

    protected PhotoListPagerViewPresenter presenter;

    @Bind(R.id.pager_photo_page)
    ViewPager pagerPhotoPage;
    @Bind(R.id.nav_view)
    NavigationView viewNavigation;
    @Bind(R.id.content_view)
    RelativeLayout contentViewLayout;

    @Bind(R.id.main_coordinator_layout)
    CoordinatorLayout mainCoordinatorLayout;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_container)
    FrameLayout toolbarContainerLayout;

    @Bind(R.id.drawee_transit)
    SimpleDraweeView transitDraweeView;

    private SystemBarTintManager kitkatTintManager;
    private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
    private WindowInsetsCompat windowInsets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initData();
        initTemplateViews();
        initViews();
        presenter.loadPageCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkToCrawlPhoto();
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.saveLastWatchedPhotoListPage(pagerPhotoPage.getCurrentItem());
    }

    private void initViews() {
        pagerPhotoPage.addOnPageChangeListener(onPhotoListPageChanged);
        pagerPhotoPage.setAdapter(adapterPhotoPages);
        pagerPhotoPage.setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);

//        transitDraweeView.setImageURI(Uri.parse("https://scontent-hkg3-1.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/12920407_536391959865990_5648965578918098818_n.jpg?oh=323d8fdd2b21166c056165b3af339b0f&oe=577D6664"));
    }

    private void initData() {
        presenter = new PhotoListPagerViewPresenter(this, photoDetailsRepository, preferenceRepository);

        kitkatTintManager = new SystemBarTintManager(this);
        kitkatSystemBarConfig = kitkatTintManager.getConfig();
    }

    private PhotoListPagerAdapter createPagerAdapter() {
        if (adapterPhotoPages == null) {
            PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(photoDetailsRepository);
            adapterPhotoPages = new PhotoListPagerAdapter(LayoutInflater.from(this), photoListingUsecase);
        }

        return adapterPhotoPages;
    }

    private Observable<Integer> createWaitWindowInsetsObservable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    L.get().d("wait windowInsets thread %s", Thread.currentThread().getName());
                    while (windowInsets == null) {

                    }

                    subscriber.onNext(windowInsets.getSystemWindowInsetBottom());
                    subscriber.onCompleted();
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return Observable.just(kitkatSystemBarConfig.getPixelInsetBottom());
        }

        return Observable.just(0);
    }

    @Override
    public void setupPagerAdapter(final int pageCount, final int type) {
        L.get().d("setupPagerAdapter");
        createWaitWindowInsetsObservable()
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer windowInsetsBottom) {
                        L.get().d("use windowInsets thread %s", Thread.currentThread().getName());
                        if (adapterPhotoPages == null) {
                            PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(photoDetailsRepository);
                            adapterPhotoPages = new PhotoListPagerAdapter(LayoutInflater.from(PhotoListPagerActivity.this), photoListingUsecase);
                            pagerPhotoPage.setAdapter(adapterPhotoPages);
                        }

                        adapterPhotoPages.setPageCount(pageCount);
                        adapterPhotoPages.setType(type);
                        adapterPhotoPages.setWindowInsetsBottom(windowInsetsBottom);
                        adapterPhotoPages.setPerPage(preferenceRepository.getListPagerPhotoPerPage());
                        adapterPhotoPages.notifyDataSetChanged();

                        presenter.loadLastWatchedPhotoListPage();
                    }
                })
                .subscribe();
    }

    private ViewPager.OnPageChangeListener onPhotoListPageChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

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
        ViewCompat.setOnApplyWindowInsetsListener(mainCoordinatorLayout, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, final WindowInsetsCompat insets) {
                toolbarContainerLayout.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                windowInsets = insets;

                return insets;
            }
        });

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            toolbarContainerLayout.setPadding(0, kitkatSystemBarConfig.getPixelInsetTop(false), 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (transitDraweeView.getVisibility() == View.VISIBLE) {
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
            changePhotoListingType(PhotoListPagerAdapter.TYPE_HOTEST);
        } else if (id == R.id.nav_latest) {
            changePhotoListingType(PhotoListPagerAdapter.TYPE_LATEST);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePhotoListingType(int type) {
        adapterPhotoPages.setType(type);
        adapterPhotoPages.notifyDataSetChanged();
        pagerPhotoPage.setAdapter(adapterPhotoPages);
    }

    @Override
    public int getCurrentType() {
        return adapterPhotoPages != null ? adapterPhotoPages.getType() : PHOTO_TYPE_DEFAULT;
    }

    @Override
    public void startActionUpdate() {
        UpdateService.startActionUpdate(this);
    }

    @Override
    public void setLastWatchedPhotoListPage(Integer page) {
        pagerPhotoPage.setCurrentItem(page, false);
    }

    /***
     * event bus
     ***/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
        presenter.loadPageCount();
    }

    @Subscribe
    public void handleOnPhotoItemClicked(final OnPhotoListItemClicked event) {
//        appBarLayout.setVisibility(View.INVISIBLE);

//        PhotoListItemAdapter.ViewHolder vh = event.getItemViewHolder();
//        pagerPhotoPage.setVisibility(View.GONE);
        transitDraweeView.setVisibility(View.VISIBLE);
        transitDraweeView.setImageURI(Uri.parse(event.getPhotoDetails().getLowResUrl()));
//        transitDraweeView.setImageResource(R.drawable.avatar_xkcn);

        L.get().d("clicked %s", event.getPhotoDetails().getLowResUrl());

//        transitImageView.setImageResource(R.drawable.avatar_xkcn);

//        RectF destRect = new RectF(0, 0, mainCoordinatorLayout.getWidth(), mainCoordinatorLayout.getHeight());
//        RectF srcRect = new RectF(locationInContentLayout.x, locationInContentLayout.y, locationInContentLayout.x + vh.ivPhoto.getWidth(), locationInContentLayout.y + vh.ivPhoto.getHeight());
//        new ZoomToAnimation()
//                .rects(srcRect, destRect)
//                .duration(175)
//                .interpolator(new AccelerateDecelerateInterpolator())
//                .target(transitImageView)
//                .run();
    }



    /*** end - event bus ***/
}
