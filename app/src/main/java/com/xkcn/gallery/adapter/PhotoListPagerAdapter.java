package com.xkcn.gallery.adapter;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xkcn.gallery.R;
import com.xkcn.gallery.presenter.PhotoListPageViewPresenter;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoListPageViewImpl;

/**
 * Created by khoinguyen on 12/23/14.
 */
public class PhotoListPagerAdapter extends PagerAdapter {
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_LATEST = 2;
    public static final int TYPE_INVALID = -1;

    protected int type;
    protected int pageCount;
    private LayoutInflater inflater;
    private PhotoListingUsecase photoListingUsecase;
    private SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig;
    private int perPage;

    public PhotoListPagerAdapter(LayoutInflater inflater, PhotoListingUsecase photoListingUsecase, SystemBarTintManager.SystemBarConfig kitkatSystemBarConfig) {
        this.inflater = inflater;
        this.photoListingUsecase = photoListingUsecase;
        this.kitkatSystemBarConfig = kitkatSystemBarConfig;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoListPageViewImpl itemView = (PhotoListPageViewImpl) inflater.inflate(R.layout.photo_list_page_view, container, false);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            itemView.setPadding(0, 0, 0, kitkatSystemBarConfig.getPixelInsetBottom());
        }

        PhotoListPageViewPresenter presenter = new PhotoListPageViewPresenter(photoListingUsecase, type, position + 1, perPage);
        presenter.setView(itemView);

        presenter.loadPhotoListPage();

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
