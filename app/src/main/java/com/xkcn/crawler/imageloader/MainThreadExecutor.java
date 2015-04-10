package com.xkcn.crawler.imageloader;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by khoinguyen on 4/10/15.
 */
public class MainThreadExecutor implements Executor {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
