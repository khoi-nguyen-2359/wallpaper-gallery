package com.xkcn.crawler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xkcn.crawler.R;
import com.xkcn.crawler.imageloader.XkcnFrescoImageLoader;
import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.presenter.PhotoSinglePageViewPresenter;
import com.xkcn.crawler.view.PhotoSinglePageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

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

    @Bind(R.id.iv_photo) ImageViewTouch ivPhoto;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    private PhotoSinglePageViewPresenter presenter;

    private Subscription photoLoadSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoLoadSubscription.unsubscribe();
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

        XkcnFrescoImageLoader.release(xkcnImageLoader, ivPhoto);

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

        ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    private Observer<Object> imageLoaderSubscriber = new Observer<Object>() {
        @Override
        public void onCompleted() {
            progressBar.setVisibility(View.GONE);
            // todo: why create no use?
//            photoDownloader.getPhotoDownloadObservable(photoDetails);
        }

        @Override
        public void onError(Throwable e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), R.string.photo_action_download_failed_retry, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(Object aBoolean) {

        }
    };

    private void loadPhoto() {
        File downloadedPhoto = photoDownloader.getDownloadFile(photoDetails.getDefaultDownloadUrl());
        if (downloadedPhoto.exists()) {
            photoLoadSubscription = xkcnImageLoader.load(downloadedPhoto, ivPhoto, imageLoaderSubscriber);
        } else {
            photoLoadSubscription = xkcnImageLoader.load(photoDetails.getPhotoHigh(), ivPhoto, imageLoaderSubscriber);
        }
    }

    @Override
    public void displayPhoto(File downloadedPhoto) {

    }
}
