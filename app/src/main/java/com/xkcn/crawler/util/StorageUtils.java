package com.xkcn.crawler.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.xkcn.crawler.XkcnApp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by khoinguyen on 2/9/15.
 */
public final class StorageUtils {
    public static final String SUFF_DOWNLOAD_TMP_FILE = "downloading.";

    public static String getResourceName(String uriString) {
        return Uri.parse(uriString).getLastPathSegment();
    }

    public static Uri savePhoto(InputStream is, String downloadUri) throws Exception {
        String fileName = getResourceName(downloadUri);
        File tmpFile = createDownloadTempFile(fileName);

        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tmpFile));
        int read = 0;
        final int len = 8192;
        byte[] buffer = new byte[len];
        while ((read = is.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        output.flush();
        output.close();

        String tmpPath = tmpFile.getAbsolutePath();
        String fixedPath = tmpPath.replace(SUFF_DOWNLOAD_TMP_FILE, "");
        File fixedFile = new File(fixedPath);
        if (!tmpFile.renameTo(fixedFile)) {
            throw new Exception("Rename download temp file failed!");
        }

        return Uri.fromFile(fixedFile);
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
        File photoDir = new File(XkcnApp.app.getExternalFilesDir(null).getAbsolutePath() + "/photo");
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
        return XkcnApp.app.getDir("photo", Context.MODE_PRIVATE);
    }

    public static File createDownloadTempFile(String fileName) {
        // first check app external storage
        File photoFile = null;
        if (isExternalStorageWritable()) {
            return new File(String.format(Locale.US, "%s/%s%s", getExternalPhotoDir().getAbsolutePath(), SUFF_DOWNLOAD_TMP_FILE, fileName));
        }

        // second check app internal storage
        return new File(String.format(Locale.US, "%s/%s%s", getPhotoDir().getAbsolutePath(), SUFF_DOWNLOAD_TMP_FILE, fileName));
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
}
