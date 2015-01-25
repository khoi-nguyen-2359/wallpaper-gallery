package com.xkcn.crawler.presenter;

import android.graphics.BitmapFactory;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
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

    public void onShareClicked() {
    }
}
