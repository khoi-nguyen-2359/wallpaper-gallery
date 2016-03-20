package com.xkcn.gallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.presenter.PhotoSinglePageViewPresenter;
import com.xkcn.gallery.view.PhotoSinglePageView;
import com.xkcn.gallery.view.custom.DraweePhotoView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class PhotoSinglePageFragment extends XkcnFragment implements PhotoSinglePageView {
    private static final String ARG_PHOTO_DETAILS = "ARG_PHOTO_DETAILS";
    private View rootView;

    public static Fragment instantiate(PhotoDetails photoDetails) {
        PhotoSinglePageFragment f = new PhotoSinglePageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO_DETAILS, photoDetails);
        f.setArguments(args);

        return f;
    }

    private PhotoDetails photoDetails;

    @Bind(R.id.iv_photo)
    DraweePhotoView ivPhoto;

    private PhotoSinglePageViewPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViews(inflater, container);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadPhoto();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    private void initData() {
        Bundle args = getArguments();
        photoDetails = args.getParcelable(ARG_PHOTO_DETAILS);

        presenter = new PhotoSinglePageViewPresenter(photoDetails, photoDownloader);
        presenter.setView(this);
    }

    private void initViews(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_photo_single_page, container, false);
        ButterKnife.bind(this, rootView);
    }

    private Observer<Object> imageLoaderSubscriber = new Observer<Object>() {
        @Override
        public void onCompleted() {
            // todo: why create no use?
//            photoDownloader.getPhotoDownloadObservable(photoDetails);
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(getContext(), R.string.photo_action_download_failed_retry, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(Object aBoolean) {
        }
    };

    private void loadPhoto() {
        ivPhoto.setImageUri(photoDetails.getDefaultDownloadUrl());
    }

    @Override
    public void displayPhoto(File downloadedPhoto) {

    }
}
