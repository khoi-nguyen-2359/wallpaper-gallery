package com.xkcn.crawler.fragment;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xkcn.crawler.PhotoSinglePagerActivity;
import com.xkcn.crawler.R;
import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;
import com.xkcn.crawler.imageloader.XkcnImageLoader;
import com.xkcn.crawler.imageloader.XkcnImageLoaderFactory;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.usecase.PhotoDownloadUsecase;
import com.xkcn.crawler.util.UiUtils;
import com.xkcn.crawler.view.PhotoActionsView;
import com.xkcn.crawler.view.PhotoSinglePageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class PhotoSinglePageFragment extends Fragment implements PhotoSinglePageView {
    private static final String ARG_PHOTO_DETAILS = "ARG_PHOTO_DETAILS";

    public static Fragment instantiate(PhotoDetails photoDetails) {
        PhotoSinglePageFragment f = new PhotoSinglePageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO_DETAILS, photoDetails);
        f.setArguments(args);

        return f;
    }

    private PhotoDownloadUsecase photoDownloadManager;
    private XkcnImageLoader xkcnImageLoader;

    private PhotoDetails photoDetails;

    @Bind(R.id.iv_photo) ImageViewTouch ivPhoto;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData();
        View rootView = inflater.inflate(R.layout.fragment_photo_single_page, container, false);
        ButterKnife.bind(this, rootView);
        initViews();

        loadPhoto();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XkcnFrescoImageLoader.release(getContext(), ivPhoto);
    }

    private void initData() {
        photoDownloadManager = PhotoDownloadUsecase.getInstance();
        xkcnImageLoader = XkcnImageLoaderFactory.getInstance(getContext());

        Bundle args = getArguments();
        photoDetails = args.getParcelable(ARG_PHOTO_DETAILS);
    }

    private void initViews() {
        ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    private XkcnImageLoader.Callback loadSingleHighPhotoCallback = new XkcnImageLoader.Callback() {
        @Override
        public void onLoaded(Bitmap bitmap) {
        }

        @Override
        public void onFailed() {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), R.string.photo_action_download_failed_retry, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCompleted() {
            progressBar.setVisibility(View.GONE);
            photoDownloadManager.createPhotoDownloadObservable(photoDetails);
        }
    };

    private void loadPhoto() {
        File downloadedPhoto = PhotoDownloadUsecase.getDownloadFile(photoDetails.getDefaultDownloadUrl());
        if (downloadedPhoto.exists()) {
            xkcnImageLoader.load(downloadedPhoto, ivPhoto, loadSingleHighPhotoCallback);
        } else {
            xkcnImageLoader.load(photoDetails.getPhotoHigh(), ivPhoto, loadSingleHighPhotoCallback);
        }
    }
}
