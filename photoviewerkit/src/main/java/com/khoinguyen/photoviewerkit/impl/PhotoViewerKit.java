package com.khoinguyen.photoviewerkit.impl;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import okhttp3.OkHttpClient;

/**
 * Created by khoinguyen on 5/3/16.
 */
public final class PhotoViewerKit {
  public static void init(Context context) {
    OkHttpClient okHttpClient = new OkHttpClient();
    ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
        .newBuilder(context, okHttpClient)
        .build();
    Fresco.initialize(context, config);
  }
}
