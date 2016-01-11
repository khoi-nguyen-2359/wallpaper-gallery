package com.xkcn.crawler.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantageek.toolkit.view.typeface.recyclerview.SimpleDividerItemDec;
import com.xkcn.crawler.PhotoSinglePagerActivity;
import com.xkcn.crawler.R;
import com.xkcn.crawler.adapter.PhotoListItemAdapter;
import com.xkcn.crawler.data.PhotoDetailsSqliteDataStore;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.event.OnPhotoListItemClicked;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.presenter.PhotoListPageViewPresenter;
import com.xkcn.crawler.usecase.PhotoListingUsecase;
import com.xkcn.crawler.view.PhotoListPageView;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoListPageFragment extends Fragment implements PhotoListPageView {
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_TYPE = "ARG_TYPE";

    private RecyclerView listPhoto;
    private PhotoListItemAdapter adapterPhotos;
    private View root;
    private int nPhotoCol;
    private int type;
    private PhotoListPageViewPresenter presenter;

    public static PhotoListPageFragment instantiate(int page, int type) {
        PhotoListPageFragment f = new PhotoListPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_TYPE, type);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_photo_page, container, false);
        initData();
        initPhotoList();
        loadPhotoListing();
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
        nPhotoCol = getResources().getInteger(R.integer.photo_page_col);
        PreferenceDataStoreImpl prefDataStore = new PreferenceDataStoreImpl();
        PhotoListingUsecase photoListingUsecase = new PhotoListingUsecase(new PhotoDetailsSqliteDataStore(), prefDataStore.getListPagerPhotoPerPage());

        presenter = new PhotoListPageViewPresenter(photoListingUsecase, getListingType(), getPage());
    }

    private void loadPhotoListing() {
        Observable<List<PhotoDetails>> photoQueryObservable = presenter.createPhotoQueryObservable();
        photoQueryObservable.subscribe(new Subscriber<List<PhotoDetails>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<PhotoDetails> photos) {
                initAdapter(photos);
            }
        });
    }

    private void initAdapter(List<PhotoDetails> photos) {
        adapterPhotos = new PhotoListItemAdapter(getActivity());
        listPhoto.setAdapter(adapterPhotos);
        adapterPhotos.setDataPhotos(photos);
        adapterPhotos.notifyDataSetChanged();
    }

    public void initPhotoList() {
        listPhoto = (RecyclerView) root.findViewById(R.id.photo_list);
        listPhoto.setHasFixedSize(true);
        listPhoto.addItemDecoration(new SimpleDividerItemDec(null, RecyclerView.VERTICAL, getResources().getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));

        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nPhotoCol, StaggeredGridLayoutManager.VERTICAL);
        listPhoto.setLayoutManager(rcvLayoutMan);
    }

    public void onEvent(OnPhotoListItemClicked event) {
        Intent intent = PhotoSinglePagerActivity.intentViewSinglePhoto(getContext(), getListingType(), getPage(), event.getClickedPosition());
        startActivity(intent);
    }

    public int getPage() {
        return getArguments().getInt(ARG_PAGE);
    }

    public int getListingType() {
        return getArguments().getInt(ARG_TYPE);
    }

}