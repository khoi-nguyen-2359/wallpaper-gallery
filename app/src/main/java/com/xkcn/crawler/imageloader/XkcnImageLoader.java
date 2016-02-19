package com.xkcn.crawler.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by khoinguyen on 4/4/15.
 */
public interface XkcnImageLoader {
    Subscription load(String url, ImageView imageView, Observer observer);
    Subscription load(File file, ImageView imageView, Observer observer);

    void load(String url, ImageView imageView);
    void load(File file, ImageView imageView);
}
