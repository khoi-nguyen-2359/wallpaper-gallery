package com.xkcn.crawler.util;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.xkcn.crawler.XkcnApp;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by khoinguyen on 12/22/14.
 */
public final class U {
    public static final String APP_PREF = "APP_PREF";
    public static final long MILISEC_A_DAY = 86400000;
    private static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";

    public static void d(String tag, String format, Object... args) {
        if (args != null && args.length != 0) {
            Log.d(tag, String.format(format, args));
        } else {
            Log.d(tag, format);
        }
    }

    /**
     *
     * @param desPath with "/" preceded
     * @param content
     */
    public static void writeToExternal(String desPath, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + desPath);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(content);
            osw.flush();
            osw.close();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNavigationBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    public static int getStatusBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    public static Point getDisplaySize(WindowManager winMan) {
        Display display = winMan.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void showSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    public static void saveLastUpdate(long lastUpdate) {
        XkcnApp.instance.getSharedPreferences(APP_PREF, 0).edit().putLong(PREF_LAST_UPDATE, lastUpdate).apply();
    }

    public static long getLastUpdate() {
        return XkcnApp.instance.getSharedPreferences(APP_PREF, 0).getLong(PREF_LAST_UPDATE, 0);
    }
}