package com.xkcn.crawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantageek.toolkit.view.typeface.recyclerview.SimpleDividerItemDec;
import com.xkcn.crawler.R;
import com.xkcn.crawler.adapter.PhotoAdapter;
import com.xkcn.crawler.adapter.PhotoPagerAdapter;
import com.xkcn.crawler.data.PreferenceDataStoreImpl;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.db.PhotoDao;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 12/22/14.
 */
public class PhotoPageFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_TYPE = "ARG_TYPE";

    private RecyclerView listPhoto;
    private PhotoAdapter adapterPhotos;
    private View root;
    private int nPhotoCol;
    private int type;
    private PreferenceDataStoreImpl prefDataStore;

    public static PhotoPageFragment instantiate(int page, int type) {
        PhotoPageFragment f = new PhotoPageFragment();
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
        populatePhotoList();
        return root;
    }

    private void initData() {
        nPhotoCol = getResources().getInteger(R.integer.photo_page_col);
        prefDataStore = new PreferenceDataStoreImpl();
    }

    private void populatePhotoList() {
        new AsyncTask<Void, Void, List<PhotoDetails>>() {
            @Override
            protected List<PhotoDetails> doInBackground(Void... params) {
                List<PhotoDetails> photoList;
                if (getArguments().getInt(ARG_TYPE) == PhotoPagerAdapter.TYPE_HOTEST) {
                    photoList = PhotoDao.queryHotest(getArguments().getInt(ARG_PAGE), prefDataStore.getListPagerPhotoPerPage());
                } else {
                    photoList = PhotoDao.queryLatest(getArguments().getInt(ARG_PAGE), prefDataStore.getListPagerPhotoPerPage());
                }

                return photoList;
            }

            @Override
            protected void onPostExecute(List<PhotoDetails> photos) {
                super.onPostExecute(photos);

                adapterPhotos.setDataPhotos(photos);
                adapterPhotos.notifyDataSetChanged();
            }
        }.execute();
    }

    public void initPhotoList() {
        listPhoto = (RecyclerView) root.findViewById(R.id.photo_list);
        listPhoto.setHasFixedSize(true);
        listPhoto.addItemDecoration(new SimpleDividerItemDec(null, RecyclerView.VERTICAL, getResources().getDimensionPixelSize(R.dimen.photo_list_pager_item_offset)));

        StaggeredGridLayoutManager rcvLayoutMan = new StaggeredGridLayoutManager(nPhotoCol, StaggeredGridLayoutManager.VERTICAL);
        listPhoto.setLayoutManager(rcvLayoutMan);
        adapterPhotos = new PhotoAdapter(getActivity());
        listPhoto.setAdapter(adapterPhotos);
    }
}