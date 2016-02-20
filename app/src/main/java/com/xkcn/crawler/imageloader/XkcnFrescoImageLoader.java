package com.xkcn.crawler.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.executors.HandlerExecutorService;
import com.facebook.common.executors.HandlerExecutorServiceImpl;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.ImmediateScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 4/4/15.
 */
public class XkcnFrescoImageLoader implements XkcnImageLoader {

    private final L logger = L.get(this);
    private WeakHashMap<ImageView, Subscription> mapImageLoadSubscriptions;

    public XkcnFrescoImageLoader(Context context) {
        init(context);
        mapImageLoadSubscriptions = new WeakHashMap<>();
    }

    public static void release(XkcnImageLoader xkcnImageLoader, Object key) {
        if (!(xkcnImageLoader instanceof XkcnFrescoImageLoader)) {
            return;
        }

        XkcnFrescoImageLoader frescoImageLoader = (XkcnFrescoImageLoader) xkcnImageLoader;
        frescoImageLoader.release(key);
    }

    private static void init(Context context) {
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

    @Override
    public Subscription load(final String url, final ImageView imageView, Observer observer) {
        Subscription subscription = mapImageLoadSubscriptions.get(imageView);
        if (subscription != null) {
            subscription.unsubscribe();
        }

        final WeakReference<ImageView> weakImageView = new WeakReference<>(imageView);
        subscription = Observable.create(new Observable.OnSubscribe<CloseableReference>() {
            @Override
            public void call(Subscriber<? super CloseableReference> subscriber) {
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(url))
                        .build();
                DataSource<CloseableReference<CloseableImage>>
                        dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
                dataSource.subscribe(new PhotoLoadSubscriber(subscriber), new HandlerExecutorServiceImpl(new Handler()));
            }
        })
                .flatMap(new Func1<CloseableReference, Observable<?>>() {
                    @Override
                    public Observable<?> call(final CloseableReference imageReference) {
                        return Observable.create(new Observable.OnSubscribe<Object>() {
                            @Override
                            public void call(Subscriber<? super Object> subscriber) {
                                ImageView ivTarget = weakImageView.get();
                                CloseableStaticBitmap imgBm = (CloseableStaticBitmap) imageReference.get();
                                Bitmap bm = imgBm.getUnderlyingBitmap();
                                ivTarget.setImageBitmap(bm);
                                track(ivTarget, imageReference);
                                subscriber.onCompleted();
                            }
                        })
                                .doOnError(new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        CloseableReference.closeSafely(imageReference);
                                    }
                                })
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mapImageLoadSubscriptions.remove(weakImageView.get());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(HandlerScheduler.from(new Handler()))
                .subscribe(observer);

        mapImageLoadSubscriptions.put(imageView, subscription);

        return subscription;
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
            subscriber.onNext(imageReference);
            subscriber.onCompleted();
        }

        @Override
        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            subscriber.onError(dataSource.getFailureCause());
        }
    }

    @Override
    public Subscription load(File file, ImageView imageView, Observer observer) {
        if (file == null) {
            return Observable.empty().subscribe(observer);
        }

        return load("file://" + file.getAbsolutePath(), imageView, observer);
    }

    @Override
    public void load(String url, ImageView imageView) {
        load(url, imageView, Observers.empty());
    }

    @Override
    public void load(File file, ImageView imageView) {
        load(file, imageView, Observers.empty());
    }
}
