package com.xkcn.crawler;

import android.content.Context;
import android.database.Cursor;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by khoinguyen on 12/26/14.
 */
public class DbHelper extends SQLiteAssetHelper {
    public static final String DB_NAME = "xkcn.db";
    public static final int DB_VERSION = 1;
    private static DbHelper instance;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        setForcedUpgrade(DB_VERSION);
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper(XkcnApp.instance);
            instance.getWritableDatabase();	// copy db from asset for the first time
        }

        return instance;
    }
}
