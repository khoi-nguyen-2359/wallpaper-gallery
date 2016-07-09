package com.xkcn.gallery.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.Semaphore;

public class LooperPreparedHandler extends Handler {

    public LooperPreparedHandler(String name) {
        this(name, Process.THREAD_PRIORITY_BACKGROUND);
    }

    protected LooperPreparedHandler(String handlerName, int handlerPriority) {
        super(startHandlerThread(handlerName, handlerPriority));
    }

    private static Looper startHandlerThread(String name, int priority) {
        final Semaphore semaphore = new Semaphore(0);
        HandlerThread handlerThread = new HandlerThread(name, priority) {
            protected void onLooperPrepared() {
                semaphore.release();
            }
        };
        handlerThread.start();
        semaphore.acquireUninterruptibly();
        return handlerThread.getLooper();
    }

    public void quit() {
        getLooper().quit();
    }

}