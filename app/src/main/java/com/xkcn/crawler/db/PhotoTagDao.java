package com.xkcn.crawler.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xkcn.crawler.util.L;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by khoinguyen on 2/22/15.
 */
public final class PhotoTagDao {
    private static L logger = L.get(PhotoTagDao.class.getSimpleName());

    public static final String TABLE_NAME = "PHOTO_TAG";
    public static final String COL_TAG = "TAG";
    private static final String COL_MARK = "MARK";

    public static int bulkInsert(HashSet<String> photoTags) {
        if (photoTags == null || photoTags.size() == 0)
            return 0;

        List<ContentValues> listCv = new ArrayList<>();
        ContentValues cv = null;
        for (String tag : photoTags) {
            cv = new ContentValues();
            cv.put(PhotoTagDao.COL_TAG, tag);
            listCv.add(cv);
        }

        return PhotoTagDao.bulkInsert(listCv);
    }

    public static int bulkInsert(List<ContentValues> listCv) {
        if (listCv == null || listCv.size() == 0)
            return 0;

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        int nAffected = 0;
        int nInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues cv : listCv) {
                int affRow = db.update(TABLE_NAME, cv, COL_TAG + "=?", new String[]{cv.getAsString(COL_TAG)});

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

    public static HashSet<String> queryTagsGroupByMark() {
        HashSet<String> result = new HashSet<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, COL_MARK, null, COL_MARK + " asc");
        if (c != null) {
            while (c.moveToNext()) {
                result.add(c.getString(0));
            }
            c.close();
        }

        return result;
    }
}
