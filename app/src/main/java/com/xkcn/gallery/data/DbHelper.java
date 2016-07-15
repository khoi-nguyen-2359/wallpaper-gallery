package com.xkcn.gallery.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by khoinguyen on 12/26/14.
 */
public class DbHelper extends SQLiteAssetHelper {
  public static final String DB_NAME = "xkcn.db";
  public static final int DB_VERSION = 1;

  public DbHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
    setForcedUpgrade(DB_VERSION);
    getWritableDatabase();
  }
}
