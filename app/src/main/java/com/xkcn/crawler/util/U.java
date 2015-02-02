package com.xkcn.crawler.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xkcn.crawler.BuildConfig;
import com.xkcn.crawler.R;
import com.xkcn.crawler.XkcnApp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by khoinguyen on 12/22/14.
 */
public final class U {
    public static final String APP_PREF = "APP_PREF";
    public static final long PERIOD_UPDATE = 86400000;
    private static final String PREF_LAST_UPDATE = "PREF_LAST_UPDATE";

    public static void dd(String format, Object... args) {
        if (!BuildConfig.LOGGABLE)
            return;

        if (args != null && args.length != 0) {
            Log.d("khoi", String.format(format, args));
        } else {
            Log.d("khoi", format);
        }
    }

    /**
     *
     * @param desPath with "/" preceded
     * @param content
     */
    public static void copyFile(String desPath, String content) {
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

    public static Uri savePhoto(InputStream is, String downloadUri) {
        try {
            String fileName = getResourceName(downloadUri);
            File photoFile = U.getWritablePhotoFile(fileName);

            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(photoFile));
            int read = 0;
            final int len = 8192;
            byte[] buffer = new byte[len];
            long time = System.currentTimeMillis();
            U.dd("start saving photo");
            while ((read = is.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            U.dd("saved photo at=%s cost %d", photoFile.getAbsolutePath(), System.currentTimeMillis() - time);

            output.flush();
            output.close();

            return Uri.fromFile(photoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void startSetWallpaperChooser(Activity activity, Uri uriImg) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uriImg, "image/jpeg");
        intent.putExtra("mimeType", "image/jpeg");

        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.photo_actions_set_wp_chooser)));
    }

    public static String getResourceName(String uriString) {
        return Uri.parse(uriString).getLastPathSegment();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getExternalPhotoDir() {
        File photoDir = new File(XkcnApp.instance.getExternalFilesDir(null).getAbsolutePath() + "/photo");
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }

        return photoDir;
    }

    public static File getWritablePhotoDir() {
        if (isExternalStorageWritable()) {
            File photoDir = getExternalPhotoDir();
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }

            return photoDir;
        }

        return getPhotoDir();
    }

    public static File getPhotoDir() {
        return XkcnApp.instance.getDir("photo", Context.MODE_PRIVATE);
    }

    public static File getWritablePhotoFile(String fileName) {
        // first check app external storage
        File photoFile = null;
        if (isExternalStorageWritable()) {
            return new File(getExternalPhotoDir().getAbsolutePath() + "/" + fileName);
        }

        // second check app internal storage
        return new File(getPhotoDir().getAbsolutePath() + "/" + fileName);
    }

    public static File getReadablePhotoFile(String uriString) {
        // first check app external storage
        String fileName = getResourceName(uriString);
        File photoFile = null;
        if (isExternalStorageReadable()) {
            photoFile = new File(getExternalPhotoDir().getAbsolutePath() + "/" + fileName);
            if (photoFile.exists()) {
                return photoFile;
            }
        }

        // second check app internal storage
        return new File(getPhotoDir().getAbsolutePath() + "/" + fileName);
    }

    public static void saveLastUpdate(long lastUpdate) {
        XkcnApp.instance.getSharedPreferences(APP_PREF, 0).edit().putLong(PREF_LAST_UPDATE, lastUpdate).apply();
    }

    public static long getLastUpdate() {
        return XkcnApp.instance.getSharedPreferences(APP_PREF, 0).getLong(PREF_LAST_UPDATE, 0);
    }
}