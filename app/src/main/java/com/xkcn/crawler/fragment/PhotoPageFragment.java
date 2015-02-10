package com.xkcn.crawler.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xkcn.crawler.R;
import com.xkcn.crawler.adapter.PhotoAdapter;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.UpdateFinishedEvent;
import com.xkcn.crawler.util.U;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoPageFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";

    private RecyclerView listPhoto;
    private PhotoAdapter adapterPhotos;
    private View root;
    private int nPhotoCol;

    public static PhotoPageFragment instantiate(int page) {
        PhotoPageFragment f = new PhotoPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_photo_page, container, false);
        initData();
        initPhotoList();
        populatePhotoList();
        return root;
    }

    private void initData() {
        nPhotoCol = getResources().getInteger(R.integer.photo_page_col);
    }

    private void populatePhotoList() {
        List<Photo> photoList = PhotoDao.query(getArguments().getInt(ARG_PAGE));
        adapterPhotos.setDataPhotos(photoList);
        adapterPhotos.notifyDataSetChanged();
    }

    public void initPhotoList() {
        listPhoto = (RecyclerView) root.findViewById(R.id.photo_list);
        listPhoto.setHasFixedSize(true);

        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nPhotoCol, StaggeredGridLayoutManager.VERTICAL);
        listPhoto.setLayoutManager(rcvLayoutMan);
        adapterPhotos = new PhotoAdapter(getActivity());
        listPhoto.setAdapter(adapterPhotos);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = U.getStatusBarHeight(getResources());
            int navBarHeight = U.getNavigationBarHeight(getResources());
            listPhoto.setPadding(0, statusBarHeight, 0, navBarHeight);
        }
    }

    public void onEventMainThread(UpdateFinishedEvent event) {
        populatePhotoList();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}