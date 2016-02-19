package com.xkcn.crawler.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.fantageek.toolkit.util.L;
import com.squareup.okhttp.OkHttpClient;
import com.xkcn.crawler.imageloader.error.NoDataSourceResultError;
import com.xkcn.crawler.imageloader.error.NoTargetImageViewError;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 4/4/15.
 */
public class XkcnFrescoImageLoader implements XkcnImageLoader {

    private final L logger = L.get(this);

    public XkcnFrescoImageLoader(Context context) {
        init(context);
    }

    public static void release(XkcnImageLoader xkcnImageLoader, Object key) {
        if (!(xkcnImageLoader instanceof XkcnFrescoImageLoader)) {
            return;
        }

        XkcnFrescoImageLoader frescoImageLoader = (XkcnFrescoImageLoader) xkcnImageLoader;
        frescoImageLoader.release(key);
    }

    public static void init(Context context) {
        final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
        final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient)
                .build();
        Fresco.initialize(context, config);
    }

    private Map<ImageView, CloseableReference<CloseableImage>> holdingReferences;

    private void track(ImageView key, CloseableReference<CloseableImage> refToTrack) {
        if (key == null || refToTrack == null) {
            return;
        }

        release(key);

        if (holdingReferences == null) {
            holdingReferences = new HashMap<>();
        }
        holdingReferences.put(key, refToTrack);
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

    public Observable loadObservable(final String url, ImageView imageView) {
        final WeakReference<ImageView> weakImageView = new WeakReference<>(imageView);
        return Observable.create(new Observable.OnSubscribe<CloseableReference>() {
            @Override
            public void call(Subscriber<? super CloseableReference> subscriber) {
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(url))
                        .build();
                DataSource<CloseableReference<CloseableImage>>
                        dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
                dataSource.subscribe(new PhotoLoadSubscriber(subscriber), CallerThreadExecutor.getInstance());
            }
        })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<CloseableReference, Observable<?>>() {
                    @Override
                    public Observable<?> call(final CloseableReference imageReference) {
                        return Observable.create(new Observable.OnSubscribe<Object>() {
                            @Override
                            public void call(Subscriber<? super Object> subscriber) {
                                ImageView ivTarget = weakImageView.get();
                                if (ivTarget == null) {
                                    subscriber.onError(new NoTargetImageViewError());
                                    return;
                                }

                                try {
                                    CloseableStaticBitmap imgBm = (CloseableStaticBitmap) imageReference.get();
                                    Bitmap bm = imgBm.getUnderlyingBitmap();
                                    ivTarget.setImageBitmap(bm);
                                    track(ivTarget, imageReference);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    CloseableReference.closeSafely(imageReference);
                                    subscriber.onError(e);
                                }
                            }
                        })
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                });
    }

    class PhotoLoadSubscriber extends BaseDataSubscriber<CloseableReference<CloseableImage>> {
        private Subscriber<? super CloseableReference> subscriber;

        PhotoLoadSubscriber(Subscriber<? super CloseableReference> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            if (!dataSource.isFinished()) {
                return;
            }

            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference == null) {
                subscriber.onError(new NoDataSourceResultError());
                return;
            }

            subscriber.onNext(imageReference);
            subscriber.onCompleted();
        }

        @Override
        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            subscriber.onError(dataSource.getFailureCause());
        }
    }

    public Observable loadObservable(File file, ImageView imageView) {
        if (file == null) {
            return Observable.empty();
        }

        return loadObservable("file://" + file.getAbsolutePath(), imageView);
    }

    @Override
    public void load(String url, ImageView imageView) {
        loadObservable(url, imageView).subscribe();
    }

    @Override
    public void load(File file, ImageView imageView) {
        loadObservable(file, imageView).subscribe();
    }
}
