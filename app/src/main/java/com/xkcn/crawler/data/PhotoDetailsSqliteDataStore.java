package com.xkcn.crawler.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkcn.crawler.db.DbHelper;
import com.xkcn.crawler.model.PhotoDetails;
import com.xkcn.crawler.util.L;

import java.util.List;

/**
 * Created by khoinguyen on 11/1/15.
 */
public class PhotoDetailsSqliteDataStore implements PhotoDetailsDataStore {
    private static L logger = L.get(PhotoDetailsSqliteDataStore.class.getSimpleName());

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

    public static PhotoDetails toPhoto(Cursor cursor) {
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
//        if ((idx = cursor.getColumnIndex(COL_DOWNLOAD_STATE)) != -1)    photo.setDownloadedState(cursor.getInt(idx));

        return photo;
    }

    @Override
    public long getLargestPhotoId() {
        return 0;
    }

    @Override
    public List<PhotoDetails> getLatestPhotos() {
        return null;
    }

    @Override
    public List<PhotoDetails> getHotestPhotos() {
        return null;
    }

    @Override
    public int addPhotos(List<PhotoDetails> photos) {
        return 0;
    }

    @Override
    public PhotoDetails getPhotoDetails(long photoId) {
        PhotoDetails photoDetails = null;

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

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
    public int getPageCount(int perPage) {
        int pageCount = 0;

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        Cursor c = db.rawQuery("select count(*)/" + perPage + " from " + TABLE_NAME, null);
        if (c != null) {
            if (c.moveToFirst()) {
                pageCount = c.getInt(0);
            }

            c.close();
        }

        return pageCount;
    }
}
