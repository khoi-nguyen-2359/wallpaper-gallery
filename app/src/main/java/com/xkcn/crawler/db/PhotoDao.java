package com.xkcn.crawler.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkcn.crawler.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 12/27/14.
 */
public final class PhotoDao {
//    public static final int DOWNLOAD_STATE_OK = 1;
//    public static final int DOWNLOAD_STATE_NONE = 0;

    private static L logger = L.get(PhotoDao.class.getSimpleName());

    public static final String TABLE_NAME = "PHOTO";
    public static final String COL_IDENTIFIER = "IDENTIFIER";
    public static final String COL_PHOTO100 = "PHOTO100";
    public static final String COL_PHOTO250 = "PHOTO250";
    public static final String COL_PHOTO500 = "PHOTO500";
    public static final String COL_PHOTO_HIGH = "PHOTO_HIGH";
    public static final String COL_PERMALINK = "PERMALINK";
    public static final String COL_PERMALINK_META = "PERMALINK_META";
    public static final String COL_NOTES_URL = "NOTES_URL";
    public static final String COL_HEIGHT_HIGH_RES = "HEIGHT_HIGH_RES";
    public static final String COL_WIDTH_HIGH_RES = "WIDTH_HIGH_RES";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_TAGS = "TAGS";
    public static final String COL_NOTES = "NOTES";
//    public static final String COL_DOWNLOAD_STATE = "DOWNLOAD_STATE";

    public static Photo toPhoto(Cursor cursor) {
        Photo photo = new Photo();
        int idx;
        if ((idx = cursor.getColumnIndex(COL_IDENTIFIER)) != -1)        photo.setIdentifier(cursor.getLong(idx));
        if ((idx = cursor.getColumnIndex(COL_PHOTO100)) != -1)          photo.setPhoto100(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_PHOTO250)) != -1)          photo.setPhoto250(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_PHOTO500)) != -1)          photo.setPhoto500(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_PHOTO_HIGH)) != -1)        photo.setPhotoHigh(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_PERMALINK)) != -1)         photo.setPermalink(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_PERMALINK_META)) != -1)    photo.setPermalinkMeta(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_NOTES_URL)) != -1)         photo.setNotesUrl(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_HEIGHT_HIGH_RES)) != -1)   photo.setHeightHighRes(cursor.getInt(idx));
        if ((idx = cursor.getColumnIndex(COL_WIDTH_HIGH_RES)) != -1)    photo.setWidthHighRes(cursor.getInt(idx));
        if ((idx = cursor.getColumnIndex(COL_TITLE)) != -1)             photo.setTitle(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_TAGS)) != -1)              photo.setTags(cursor.getString(idx));
        if ((idx = cursor.getColumnIndex(COL_NOTES)) != -1)             photo.setNotes(cursor.getInt(idx));
//        if ((idx = cursor.getColumnIndex(COL_DOWNLOAD_STATE)) != -1)    photo.setDownloadedState(cursor.getInt(idx));

        return photo;
    }

    public static ContentValues toContentValues(Photo photo) {
        ContentValues cv = new ContentValues();
        cv.put(COL_IDENTIFIER, photo.getIdentifier());
        cv.put(COL_PHOTO100, photo.getPhoto100());
        cv.put(COL_PHOTO250, photo.getPhoto250());
        cv.put(COL_PHOTO500, photo.getPhoto500());
        cv.put(COL_PHOTO_HIGH, photo.getPhotoHigh());
        cv.put(COL_PERMALINK, photo.getPermalink());
        cv.put(COL_PERMALINK_META, photo.getPermalinkMeta());
        cv.put(COL_NOTES_URL, photo.getNotesUrl());
        cv.put(COL_HEIGHT_HIGH_RES, photo.getHeightHighRes());
        cv.put(COL_WIDTH_HIGH_RES, photo.getWidthHighRes());
        cv.put(COL_TITLE, photo.getTitle());
        cv.put(COL_TAGS, photo.getTags());
        cv.put(COL_NOTES, photo.getNotes());
//        cv.put(COL_DOWNLOAD_STATE, photo.getDownloadedState());

        return cv;
    }

    public static long getLargestPhotoId() {
        long largestPhotoId = 0;

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, new String[]{COL_IDENTIFIER}, null, null, null, null, COL_IDENTIFIER + " desc", "1");
        if (c != null) {
            if (c.moveToNext()) {
                largestPhotoId = c.getLong(0);
            }

            c.close();
        }

        return largestPhotoId;
    }

    public static List<Photo> queryLatest(int page) {
        List<Photo> photoList = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, COL_IDENTIFIER + " desc", ((page - 1) * 15) + ", 15");
        if (c != null) {
            Photo photo = null;
            while (c.moveToNext()) {
                photo = toPhoto(c);
                photoList.add(photo);
            }

            c.close();
        }

        return photoList;
    }

    public static List<Photo> queryHotest(int page) {
        List<Photo> photoList = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, COL_NOTES + " desc", ((page - 1) * 15) + ",15");
        if (c != null) {
            Photo photo = null;
            while (c.moveToNext()) {
                photo = toPhoto(c);
                photoList.add(photo);
            }

            c.close();
        }

        return photoList;
    }

    public static int bulkInsertPhoto(List<Photo> photoList) {
        if (photoList == null || photoList.size() == 0)
            return 0;

        List<ContentValues> listCv = new ArrayList<>();
        ContentValues cv = null;
        for (Photo photo : photoList) {
            cv = toContentValues(photo);
            listCv.add(cv);
        }

        return bulkInsert(listCv);
    }

    private static int bulkInsert(List<ContentValues> listCv) {
        if (listCv == null || listCv.size() == 0)
            return 0;

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        int nAffected = 0;
        int nInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues cv : listCv) {
                int affRow = db.update(TABLE_NAME, cv, COL_IDENTIFIER + "=?", new String[]{cv.getAsString(COL_IDENTIFIER)});

                // update tried failed, do insert
                if (affRow == 0) {
                    long newId = db.insert(TABLE_NAME, null, cv);
                    affRow = newId != -1 ? 1 : 0;
                    nInserted += affRow;
                }

                nAffected += affRow;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        logger.d("bulkInsert affected=%d", nAffected);
        logger.d("bulkInsert inserted=%d", nInserted);

        return nInserted;
    }
}
