package com.xkcn.crawler.imageloader;

import android.content.Context;

/**
 * Created by khoinguyen on 4/10/15.
 */
public final class XkcnImageLoaderFactory {
    private static XkcnImageLoader instance;

    public static XkcnImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new XkcnFrescoImageLoader();
            XkcnFrescoImageLoader.init(context);
        }

        return instance;
    }
}
