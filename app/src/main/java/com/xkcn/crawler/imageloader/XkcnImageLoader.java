package com.xkcn.crawler.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by khoinguyen on 4/4/15.
 */
public interface XkcnImageLoader {
    interface Callback {
        void onLoaded(Bitmap bitmap);
        void onFailed();
        void onCompleted();
    }

    void load(String uri, ImageView imageView, Callback callback);
    void load(File file, ImageView imageView, Callback callback);
}
