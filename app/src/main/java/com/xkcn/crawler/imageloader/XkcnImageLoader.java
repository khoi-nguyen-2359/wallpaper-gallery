package com.xkcn.crawler.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

import rx.Observable;

/**
 * Created by khoinguyen on 4/4/15.
 */
public interface XkcnImageLoader {
    Observable loadObservable(String url, ImageView imageView);
    Observable loadObservable(File file, ImageView imageView);

    void load(String url, ImageView imageView);
    void load(File file, ImageView imageView);
}
