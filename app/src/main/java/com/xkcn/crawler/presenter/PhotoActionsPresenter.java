package com.xkcn.crawler.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xkcn.crawler.R;
import com.xkcn.crawler.XkcnApp;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.util.U;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by khoinguyen on 1/25/15.
 */
public class PhotoActionsPresenter {
    private Photo photo;
    private OkHttpClient httpClient;

    private OkHttpClient getHttpClient() {
        if (httpClient == null)
            httpClient = new OkHttpClient();

        return httpClient;
    }

    public PhotoActionsPresenter() {
    }

    public void onDownloadClicked() {
        OkHttpClient client = getHttpClient();
        Request request = new Request.Builder()
                .url(photo.getPhotoHigh())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onPhotoDownloadFailed();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onPhotoDownloadFailed();
                    return;
                }

                InputStream is = response.body().byteStream();
                U.savePhoto(is, photo.getPhotoHigh());
                is.close();
            }
        });
    }

    private void onPhotoDownloadFailed() {
        U.dd("onPhotoDownloadFailed");
    }

    public void onSetWallpaperClicked() {
        File fileDownloaded = U.getReadablePhotoFile(photo.getPhotoHigh());
        if (fileDownloaded.exists()) {
            U.dd("file already downloaded");

            try {
                FileInputStream fis = new FileInputStream(fileDownloaded);
                U.setWallpaper(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            U.dd("file downloading");

            OkHttpClient client = getHttpClient();
            final Request request = new Request.Builder()
                    .url(photo.getPhotoHigh())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    onPhotoDownloadFailed();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        onPhotoDownloadFailed();
                        return;
                    }

                    InputStream is = response.body().byteStream();
                    U.setWallpaper(is);
                    is.close();
                }
            });
        }
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void onShareClicked(Context context) {
        String sendText = null;
        if (TextUtils.isEmpty(photo.getPermalinkMeta())) {
            sendText = XkcnApp.instance.getString(R.string.send_to_trailing_text, photo.getPermalink());
        } else {
            Spanned spanned = Html.fromHtml(photo.getPermalinkMeta());
            sendText = XkcnApp.instance.getString(R.string.send_to_trailing_text, spanned.toString() + " " + photo.getPermalink());;
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, sendText);
        i.setType("text/plain");

        context.startActivity(Intent.createChooser(i, XkcnApp.instance.getString(R.string.send_to)));
    }
}