package com.xkcn.crawler;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.event.PhotoDownloadFailedEvent;
import com.xkcn.crawler.event.PhotoDownloadedEvent;
import com.xkcn.crawler.event.SetWallpaperClicked;
import com.xkcn.crawler.photomanager.PhotoDownloadSubscriber;
import com.xkcn.crawler.photomanager.PhotoDownloadManager;
import com.xkcn.crawler.util.U;
import com.xkcn.crawler.util.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by khoinguyen on 1/21/15.
 */
public class BaseActivity extends ActionBarActivity {

    private Dialog proDlg;

    private PhotoDownloadSubscriber setWallpaperEventSubscriber = new PhotoDownloadSubscriber() {
        @Override
        public void onEventMainThread(PhotoDownloadedEvent e) {
            UiUtils.dismissDlg(proDlg);
            U.startSetWallpaperChooser(BaseActivity.this, e.getDownloadedUri());
        }

        @Override
        public void onEventMainThread(PhotoDownloadFailedEvent e) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(setWallpaperEventSubscriber);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(PhotoDownloadFailedEvent e) {
        UiUtils.dismissDlg(proDlg);
        Toast.makeText(BaseActivity.this, R.string.photo_action_download_failed_retry, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(SetWallpaperClicked e) {
        final Photo photo = e.getPhoto();
        final PhotoDownloadManager photoDownloadManager = PhotoDownloadManager.getInstance();
        if (!EventBus.getDefault().isRegistered(setWallpaperEventSubscriber)) {
            EventBus.getDefault().register(setWallpaperEventSubscriber);
        }
        if (!photoDownloadManager.asyncDownload(photo.getIdentifier(), photo.getPhotoHigh())) {
            proDlg = UiUtils.showSimpleProgressDlg(this, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().unregister(setWallpaperEventSubscriber);
                }
            });
        }
    }
}
