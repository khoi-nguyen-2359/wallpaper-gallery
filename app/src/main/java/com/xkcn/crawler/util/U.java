package com.xkcn.crawler.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.xkcn.crawler.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by khoinguyen on 12/22/14.
 */
public final class U {
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

    public static void startSetWallpaperChooser(Activity activity, Uri uriImg) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uriImg, "image/jpeg");
        intent.putExtra("mimeType", "image/jpeg");

        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.photo_actions_set_wp_chooser)));
    }
}