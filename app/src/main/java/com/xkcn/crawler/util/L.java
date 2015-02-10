package com.xkcn.crawler.util;

import android.util.Log;

import com.xkcn.crawler.BuildConfig;

import java.util.HashMap;

/**
 * Created by khoinguyen on 2/4/15.
 */
public final class L {
    private static HashMap<String, L> instances;
    private static HashMap<String, L> getInstances() {
        if (instances == null) {
            instances = new HashMap<>();
        }

        return instances;
    }

    public static L get() {
        return get("khoi");
    }

    public static L get(String tag) {
        HashMap<String, L> instances = getInstances();
        L l = instances.get(tag);
        if (l == null) {
            l = new L(tag);
            instances.put(tag, l);
        }

        return l;
    }

    private String tag;
    private L(String tag) {
        this.tag = tag;
    }

    public void d(String format, Object... args) {
        if (!BuildConfig.LOGGABLE)
            return;

        try {
            if (args != null && args.length != 0) {
                Log.d(tag, String.format(format, args));
            } else {
                Log.d(tag, format);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
