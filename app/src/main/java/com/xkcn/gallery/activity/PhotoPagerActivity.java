package com.xkcn.gallery.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xkcn.gallery.R;
import com.xkcn.gallery.event.SetWallpaperClicked;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.presenter.PhotoListingViewPresenter;
import com.xkcn.gallery.util.AndroidUtils;
import com.xkcn.gallery.util.UiUtils;
import com.xkcn.gallery.view.PhotoListingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by khoinguyen on 1/21/15.
 */
public abstract class PhotoPagerActivity extends XkcnActivity implements PhotoListingView {

    protected Dialog proDlg;
    protected PhotoListingViewPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new PhotoListingViewPresenter(photoDownloader);
        presenter.setView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /*=====*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SetWallpaperClicked event) {
        PhotoDetails photoDetails = event.getPhoto();
        presenter.loadWallpaperSetting(photoDetails);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(PhotoPagerActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        proDlg = ProgressDialog.show(this, null, getString(R.string.msg_wait_a_moment), true);
    }

    @Override
    public void hideLoading() {
        UiUtils.dismissDlg(proDlg);
    }

    @Override
    public void showWallpaperChooser(File photoFile) {
        Uri uri = Uri.fromFile(photoFile);
        AndroidUtils.startSetWallpaperChooser(this, uri);
    }
    /*=====*/
}
