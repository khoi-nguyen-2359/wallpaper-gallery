package com.xkcn.gallery.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.data.model.ModelConstants;
import com.xkcn.gallery.data.model.PhotoDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class PhotoDetailsSqliteRepository implements PhotoDetailsRepository {
    private static L logger = L.get(PhotoDetailsSqliteRepository.class.getSimpleName());

    private static final int PHOTO_PER_PAGE = 15;

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
    public static final String COL_STATUS = "STATUS";

    public PhotoDetails toPhoto(Cursor cursor) {
        PhotoDetails photo = new PhotoDetails();
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
        if ((idx = cursor.getColumnIndex(COL_STATUS)) != -1)            photo.setStatus(cursor.getInt(idx));

        return photo;
    }

    public ContentValues toContentValues(PhotoDetails photo) {
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
        cv.put(COL_STATUS, photo.getStatus());

        return cv;
    }

    private final SQLiteOpenHelper dbHelper;

    public PhotoDetailsSqliteRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<PhotoDetails> getLatestPhotos(int page, int perPage) {
        List<PhotoDetails> photoList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, COL_STATUS+"="+ ModelConstants.PHOTO_STATUS_CRAWLED, null, null, null, COL_IDENTIFIER + " desc", ((page - 1) * perPage) + "," + perPage);
        if (c != null) {
            PhotoDetails photo = null;
            while (c.moveToNext()) {
                photo = toPhoto(c);
                photoList.add(photo);
            }

            c.close();
        }

        return photoList;    }

    @Override
    public List<PhotoDetails> getHotestPhotos(int page, int perPage) {
        List<PhotoDetails> photoList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, COL_STATUS+"="+ ModelConstants.PHOTO_STATUS_CRAWLED, null, null, null, COL_NOTES + " desc", ((page - 1) * perPage) + "," + perPage);
        if (c != null) {
            PhotoDetails photo = null;
            while (c.moveToNext()) {
                photo = toPhoto(c);
                photoList.add(photo);
            }

            c.close();
        }

        return photoList;
    }

    @Override
    public PhotoDetails getPhotoDetails(long photoId) {
        PhotoDetails photoDetails = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, COL_IDENTIFIER+"=?", new String[]{photoId+""}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                photoDetails = toPhoto(c);
            }

            c.close();
        }

        return photoDetails;
    }

    @Override
    public int updatePhotosStatus(int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, status);
        int nAffected = db.update(TABLE_NAME, cv, null, null);
        logger.d("updatePhotosStatus affected=%d", nAffected);

        return nAffected;
    }

    @Override
    public int getPageCount(int perPage) {
        int pageCount = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select count(*)/" + perPage + " from " + TABLE_NAME, null);
        if (c != null) {
            if (c.moveToFirst()) {
                pageCount = c.getInt(0);
            }

            c.close();
        }

        return pageCount;
    }

    public long getLargestPhotoId() {
        long largestPhotoId = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, new String[]{COL_IDENTIFIER}, null, null, null, null, COL_IDENTIFIER + " desc", "1");
        if (c != null) {
            if (c.moveToNext()) {
                largestPhotoId = c.getLong(0);
            }

            c.close();
        }

        return largestPhotoId;
    }

    @Override
    public int addPhotos(List<PhotoDetails> photoList) {
        if (photoList == null || photoList.size() == 0)
            return 0;

        List<ContentValues> listCv = new ArrayList<>();
        ContentValues cv = null;
        for (PhotoDetails photo : photoList) {
            cv = toContentValues(photo);
            listCv.add(cv);
        }

        return bulkInsert(listCv);
    }

    private int bulkInsert(List<ContentValues> listCv) {
        if (listCv == null || listCv.size() == 0)
            return 0;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

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
