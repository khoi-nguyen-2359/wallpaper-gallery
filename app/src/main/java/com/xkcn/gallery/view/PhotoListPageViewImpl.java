package com.xkcn.gallery.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.List;

/**
 * Created by khoinguyen on 3/29/16.
 */
public class PhotoListPageViewImpl extends RecyclerView implements PhotoListPageView {
    private PhotoListItemAdapter adapterPhotos;

    public PhotoListPageViewImpl(Context context) {
        super(context);
        init();
    }

    public PhotoListPageViewImpl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoListPageViewImpl(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHasFixedSize(true);

        Resources resources = getResources();
        int nLayoutCol = resources.getInteger(R.integer.photo_page_col);
        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nLayoutCol, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(rcvLayoutMan);
        addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));
    }

    @Override
    public void setupPagerAdapter(List<PhotoDetails> photos) {
        if (adapterPhotos == null) {
            adapterPhotos = new PhotoListItemAdapter(getContext());
            setAdapter(adapterPhotos);
        }

        adapterPhotos.setDataPhotos(photos);
        adapterPhotos.notifyDataSetChanged();
    }
}
