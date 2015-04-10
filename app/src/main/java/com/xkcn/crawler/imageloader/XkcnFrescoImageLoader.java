package com.xkcn.crawler.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by khoinguyen on 4/4/15.
 */
public class XkcnFrescoImageLoader implements XkcnImageLoader {
    public static void release(Context context, Object key) {
        XkcnImageLoader instance = XkcnImageLoaderFactory.getInstance(context);
        if (instance instanceof XkcnFrescoImageLoader) {
            XkcnFrescoImageLoader frescoInstance = (XkcnFrescoImageLoader) instance;
            frescoInstance.release(key);
        }
    }

    public static void init(Context context) {
        final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, client)
                .build();
        Fresco.initialize(context, config);
    }

    private Map<ImageView, CloseableReference<CloseableImage>> holdingReferences;
    private Executor callbackExecutor;

    XkcnFrescoImageLoader() {
        callbackExecutor = new MainThreadExecutor();
    }

    private void track(ImageView key, CloseableReference<CloseableImage> ref) {
        if (key == null || ref == null) {
            return;
        }

        if (holdingReferences == null) {
            holdingReferences = new HashMap<>();
        }

        CloseableReference<CloseableImage> reference = holdingReferences.get(key);
        CloseableReference.closeSafely(reference);

        holdingReferences.put(key, reference);
    }

    /**
     * Release all references being hold in object key
     * @param key
     */
    public void release(Object key) {
        if (key == null || holdingReferences == null) {
            return;
        }

        CloseableReference<CloseableImage> references = holdingReferences.get(key);
        CloseableReference.closeSafely(references);

        holdingReferences.remove(key);
    }

    @Override
    public void load(String uri, ImageView imageView, XkcnImageLoader.Callback callback) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(uri))
                .build();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        final RequestInfo reqInfo = new RequestInfo(imageView, callback);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableImage> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    try {
                        CloseableStaticBitmap imgBm = (CloseableStaticBitmap) imageReference.get();
                        Bitmap bm = imgBm.getUnderlyingBitmap();
                        if (reqInfo.targetRef.get() != null) {
                            reqInfo.targetRef.get().setImageBitmap(bm);
                        }
                        if (reqInfo.callbackRef.get() != null) {
                            reqInfo.callbackRef.get().onCompleted();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (reqInfo.targetRef.get() != null) {
                            track(reqInfo.targetRef.get(), imageReference);
                        }
                    }
                }

                reqInfo.clear();
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (reqInfo.callbackRef.get() != null) {
                    reqInfo.callbackRef.get().onFailed();
                }

                reqInfo.clear();
            }
        }, callbackExecutor);
    }

    @Override
    public void load(File file, ImageView imageView, Callback callback) {
        if (file == null) {
            return;
        }

        load("file://" + file.getAbsolutePath(), imageView, callback);
    }

    static class RequestInfo {
        WeakReference<ImageView> targetRef;
        WeakReference<Callback> callbackRef;

        RequestInfo(ImageView imageViewTarget, Callback callback) {
            targetRef = new WeakReference<>(imageViewTarget);
            callbackRef = new WeakReference<>(callback);
        }

        void clear() {
            if (targetRef != null) {
                targetRef.clear();
            }
            if (callbackRef != null) {
                callbackRef.clear();
            }
        }
    }
}
