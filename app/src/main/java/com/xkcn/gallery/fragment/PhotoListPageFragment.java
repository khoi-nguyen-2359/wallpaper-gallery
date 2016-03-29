package com.xkcn.gallery.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khoinguyen.logging.L;
import com.khoinguyen.recyclerview.SimpleDividerItemDec;
import com.xkcn.gallery.R;
import com.xkcn.gallery.adapter.PhotoListItemAdapter;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;
import com.xkcn.gallery.presenter.PhotoListPageViewPresenter;
import com.xkcn.gallery.usecase.PhotoListingUsecase;
import com.xkcn.gallery.view.PhotoListPageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by khoinguyen on 12/22/14.
 */
public abstract class PhotoListPageFragment extends XkcnFragment implements PhotoListPageView {
    protected static final String ARG_PAGE = "ARG_PAGE";
    protected static final String ARG_TYPE = "ARG_TYPE";

    protected RecyclerView listPhoto;
    protected PhotoListItemAdapter adapterPhotos;
    protected View root;
    protected int nPhotoCol;
    protected int type;
    protected PhotoListPageViewPresenter presenter;
    private L logger;

    public static PhotoListPageFragment instantiate(int page, int type) {
        PhotoListPageFragment f = new PhotoListPageFragmentImpl();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_TYPE, type);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(inflater, container);
        presenter.loadPhotoListPage();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
//        nPhotoCol = getResources().getInteger(R.integer.photo_page_col);
//        PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(photoDetailsRepository, preferenceRepository.getListPagerPhotoPerPage());
//
//        presenter = new PhotoListPageViewPresenter(photoListingUsecase, getListingType(), getPage(), perPage);
//        presenter.setView(this);
//
//        logger = L.get(getClass().getSimpleName());
    }

    @Override
    public void setupPagerAdapter(List<PhotoDetails> photos) {
        if (adapterPhotos == null) {
            adapterPhotos = new PhotoListItemAdapter(getActivity());
            listPhoto.setAdapter(adapterPhotos);
        }

        adapterPhotos.setDataPhotos(photos);
        adapterPhotos.notifyDataSetChanged();
    }

    public View initView(LayoutInflater inflater, ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_photo_page, container, false);

        listPhoto = (RecyclerView) root.findViewById(R.id.photo_list);
        listPhoto.setHasFixedSize(true);

        Resources resources = getResources();
        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nPhotoCol, StaggeredGridLayoutManager.VERTICAL);
        listPhoto.setLayoutManager(rcvLayoutMan);
        listPhoto.addItemDecoration(new SimpleDividerItemDec(null, StaggeredGridLayoutManager.VERTICAL, resources.getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));

        return root;
    }

    public int getPage() {
        return getArguments().getInt(ARG_PAGE);
    }

    public int getListingType() {
        return getArguments().getInt(ARG_TYPE);
    }

    /**
     * event bus
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PhotoCrawlingFinishedEvent event) {
        presenter.loadPhotoListPage();
    }

    /** END - event bus **/
}