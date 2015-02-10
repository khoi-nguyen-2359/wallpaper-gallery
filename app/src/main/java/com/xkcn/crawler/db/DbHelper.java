package com.xkcn.crawler.db;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.xkcn.crawler.XkcnApp;

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
            instance = new DbHelper(XkcnApp.app);
            instance.getWritableDatabase();	// copy db from asset for the first time
        }

        return instance;
    }
}
