package com.xkcn.crawler.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xkcn.crawler.R;
import com.xkcn.crawler.event.SetWallpaperClicked;
import com.xkcn.crawler.data.model.PhotoDetails;
import com.xkcn.crawler.presenter.PhotoListingViewPresenter;
import com.xkcn.crawler.util.AndroidUtils;
import com.xkcn.crawler.util.UiUtils;
import com.xkcn.crawler.view.PhotoListingView;

import de.greenrobot.event.EventBus;

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
    public void showWallpaperChooser(String photoUrl) {
        Uri uri = Uri.fromFile(photoDownloader.getDownloadFile(photoUrl));
        AndroidUtils.startSetWallpaperChooser(this, uri);
    }
    /*=====*/
}
