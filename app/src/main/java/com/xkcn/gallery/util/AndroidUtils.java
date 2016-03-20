package com.xkcn.gallery.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.xkcn.gallery.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by khoinguyen on 12/22/14.
 */
public final class AndroidUtils {
    /**
     *
     */
    public static void copyFile(String des, String src) {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + des);
            FileInputStream fis = new FileInputStream(src);
            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = fis.read(buffer))>0){
                fos.write(buffer, 0, read);
            }
            fos.flush();
            fos.close();
            fis.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
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
}