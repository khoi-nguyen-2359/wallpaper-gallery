package com.xkcn.gallery.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.khoinguyen.logging.L;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.adapter.PhotoListingPagerAdapter;
import com.xkcn.gallery.data.PhotoDetailsRepository;
import com.xkcn.gallery.data.PreferenceRepository;
import com.xkcn.gallery.presenter.MainViewPresenter;
import com.xkcn.gallery.presenter.PhotoListingViewPagerPresenter;
import com.xkcn.gallery.usecase.PhotoListingUsecase;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoListingViewPagerImpl extends ViewPager implements PhotoListingViewPager {
    private static final int DEF_OFFSCREEN_PAGE = 1;

    private WindowInsetsCompat windowInsets;
    private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
    private PhotoListingPagerAdapter adapterPhotoPages;
    private PhotoDetailsRepository photoDetailsRepository;
    private PreferenceRepository preferenceRepository;
    private PhotoListingViewPagerPresenter presenter;

    public PhotoListingViewPagerImpl(Context context) {
        super(context);
        init();
    }

    private void init() {
        addOnPageChangeListener(onPhotoListPageChanged);
        setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);
    }

    public PhotoListingViewPagerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private OnPageChangeListener onPhotoListPageChanged = new OnPageChangeListener() {
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

    @Override
    public void displayPhotoPages(final int pageCount, final int type) {
        waitWindowInsets()
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer windowInsetsBottom) {
                        if (adapterPhotoPages == null) {
                            PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(photoDetailsRepository);
                            adapterPhotoPages = new PhotoListingPagerAdapter(LayoutInflater.from(getContext()), photoListingUsecase);
                            setAdapter(adapterPhotoPages);
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

    @Override
    public void setCurrentPage(int page) {
        setCurrentItem(page, false);
    }

    @Override
    public int getCurrentPage() {
        return getCurrentItem();
    }

    @Override
    public void onApplyWindowInsets(WindowInsetsCompat insets) {
        windowInsets = insets;
    }

    private Observable<Integer> waitWindowInsets() {
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

    public void setKitkatSystemBarConfig(SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig) {
        this.kitkatSystemBarConfig = kitkatSystemBarConfig;
    }

    public void setPreferenceRepository(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public void setPhotoDetailsRepository(PhotoDetailsRepository photoDetailsRepository) {
        this.photoDetailsRepository = photoDetailsRepository;
    }

    public void changeListingType(int type) {
        adapterPhotoPages.setType(type);
        adapterPhotoPages.notifyDataSetChanged();
        setAdapter(adapterPhotoPages);
    }

    public void setPresenter(PhotoListingViewPagerPresenter presenter) {
        this.presenter = presenter;
    }
}
