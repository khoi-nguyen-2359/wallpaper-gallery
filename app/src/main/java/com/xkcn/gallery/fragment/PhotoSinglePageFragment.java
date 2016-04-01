package com.xkcn.gallery.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;
import com.khoinguyen.logging.L;
import com.xkcn.gallery.R;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.event.CurrentPhotoPageLoaded;
import com.xkcn.gallery.event.PagerSinglePhotoSelected;
import com.xkcn.gallery.presenter.PhotoSinglePageViewPresenter;
import com.xkcn.gallery.view.PhotoSinglePageView;
import com.xkcn.gallery.view.PhotoSinglePagerView;
import com.xkcn.gallery.view.custom.DashLineProgressDrawable;
import com.xkcn.gallery.view.custom.draweephoto.ZoomableDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by khoinguyen on 12/15/15.
 */
public class PhotoSinglePageFragment extends XkcnFragment implements PhotoSinglePageView {
    private static final String ARG_PHOTO_DETAILS = "ARG_PHOTO_DETAILS";

    public static Fragment instantiate(PhotoDetails photoDetails) {
        PhotoSinglePageFragment f = new PhotoSinglePageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO_DETAILS, photoDetails);
        f.setArguments(args);

        return f;
    }

    private View rootView;

    private PhotoDetails photoDetails;

    @Bind(R.id.iv_photo)
    ZoomableDraweeView ivPhoto;

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
        EventBus.getDefault().register(photoLoadingEventListener);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startLoadPhotoOnViewCreated();
    }

    private void startLoadPhotoOnViewCreated() {
        PhotoSinglePagerView.PhotoPagerLoadingTracker photoLoadingTracker = getPhotoLoadingTracker();
        if (photoLoadingTracker == null) {
            return;
        }

        if (photoLoadingTracker.isCurrentPhoto(photoDetails.getIdentifier()) || photoLoadingTracker.getCurrentPhotoStatus() == PhotoSinglePagerView.PhotoPagerLoadingTracker.STATUS_LOADED) {
            startLoadPhoto();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(photoLoadingEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        resetPhotoStatus();
    }

    private void resetPhotoStatus() {
        PhotoSinglePagerView.PhotoPagerLoadingTracker photoLoadingTracker = getPhotoLoadingTracker();
        if (photoLoadingTracker == null) {
            return;
        }

        photoLoadingTracker.setPhotoStatus(photoDetails.getIdentifier(), PhotoSinglePagerView.PhotoPagerLoadingTracker.STATUS_UNSTARTED);
    }

    private PhotoSinglePagerView.PhotoPagerLoadingTracker getPhotoLoadingTracker() {
        if (getParentFragment() != null && getParentFragment() instanceof PhotoSinglePagerView) {
            return ((PhotoSinglePagerView) getParentFragment()).getPhotoPagerLoadingTracker();
        }

        if (getActivity() != null && getActivity() instanceof PhotoSinglePagerView) {
            return ((PhotoSinglePagerView) getActivity()).getPhotoPagerLoadingTracker();
        }

        return null;
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

        Resources resources = getResources();
        GenericDraweeHierarchy hierachy = ivPhoto.getHierarchy();
        DashLineProgressDrawable progressDrawable = new DashLineProgressDrawable();
        progressDrawable.setElapsedProgressColor(resources.getColor(R.color.xkcn_avatar_pink));
        progressDrawable.setBackgroundColor(Color.BLACK);
        progressDrawable.setRemainedProgressColor(resources.getColor(android.R.color.darker_gray));
        progressDrawable.setBarWidth(resources.getDimensionPixelSize(R.dimen.photo_single_page_progress_bar_width));
        hierachy.setProgressBarImage(progressDrawable);

        ivPhoto.setListener(photoLoadListener);
    }

    private void startLoadPhoto() {
        PhotoSinglePagerView.PhotoPagerLoadingTracker photoPagerLoadingTracker = getPhotoLoadingTracker();
        if (photoPagerLoadingTracker == null) {
            return;
        }

        if (photoPagerLoadingTracker.getPhotoStatus(photoDetails.getIdentifier()) != PhotoSinglePagerView.PhotoPagerLoadingTracker.STATUS_UNSTARTED) {
            return;
        }

        photoPagerLoadingTracker.setPhotoStatus(photoDetails.getIdentifier(), PhotoSinglePagerView.PhotoPagerLoadingTracker.STATUS_STARTED_LOADING);
        ivPhoto.setImageUrl(photoDetails.getDefaultDownloadUrl());
    }

    @Override
    public void displayPhoto(File downloadedPhoto) {

    }
    
    private BaseControllerListener<ImageInfo> photoLoadListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            PhotoSinglePagerView.PhotoPagerLoadingTracker photoPagerLoadingTracker = getPhotoLoadingTracker();
            if (photoPagerLoadingTracker == null) {
                return;
            }

            if (photoPagerLoadingTracker.isCurrentPhoto(photoDetails.getIdentifier())) {
                EventBus.getDefault().post(new CurrentPhotoPageLoaded());
                L.get(PhotoSinglePageFragment.class).d("loaded current photo");
            } else {
                L.get(PhotoSinglePageFragment.class).d("loaded side photo");
            }

            photoPagerLoadingTracker.setPhotoStatus(photoDetails.getIdentifier(), PhotoSinglePagerView.PhotoPagerLoadingTracker.STATUS_LOADED);
        }
    };

    private Object photoLoadingEventListener = new Object() {
        @Subscribe
        public void handleCurrentPhotoPageLoaded(CurrentPhotoPageLoaded event) {
            startLoadPhoto();
        }

        @Subscribe
        public void handlePagerSinglePhotoSelected(PagerSinglePhotoSelected event) {
            if (!getPhotoLoadingTracker().isCurrentPhoto(photoDetails.getIdentifier())) {
                return;
            }

            startLoadPhoto();
        }
    };
}
