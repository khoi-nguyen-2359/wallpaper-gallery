package com.xkcn.gallery.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xkcn.gallery.R;
import com.xkcn.gallery.di.PhotoComponent;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoListingRecyclerView;

import javax.inject.Inject;

/**
 * Created by khoinguyen on 12/23/14.
 */
public class PhotoListingPagerAdapter extends PagerAdapter {
    public static final int TYPE_HOTEST = 1;
    public static final int TYPE_LATEST = 2;
    public static final int TYPE_INVALID = -1;

    protected int type;
    protected int pageCount;
    private LayoutInflater inflater;

    private PhotoComponent photoComponent;

    private int perPage;
    private int windowInsetsBottom;

    public PhotoListingPagerAdapter(PhotoComponent photoComponent) {
        this.photoComponent = photoComponent;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (inflater == null) {
            inflater = LayoutInflater.from(container.getContext());
        }

        PhotoListingRecyclerView itemView = (PhotoListingRecyclerView) inflater.inflate(R.layout.photo_list_page_view, container, false);
        itemView.setPadding(0, 0, 0, windowInsetsBottom);

        PhotoListingViewPresenter presenter = new PhotoListingViewPresenter(perPage);
        photoComponent.inject(presenter);
        presenter.setView(itemView);

        presenter.loadPhotoListPage(position + 1, type);

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

    public void setWindowInsetsBottom(int windowInsetsBottom) {
        this.windowInsetsBottom = windowInsetsBottom;
    }
}
