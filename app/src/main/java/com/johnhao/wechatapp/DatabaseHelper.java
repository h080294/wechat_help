package com.johnhao.wechatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "config.db";
    private static final int DATABASE_VERSION = 1;
    public static final String CONFIG_TABLE_NAME = "setting";

    public static final String CREATE_CONFIG = "create table if not exists " + CONFIG_TABLE_NAME + "("
            + Config._ID + " INTEGER PRIMARY KEY,"
            + "name text, "
            + "value text )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONFIG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // 删除表
//        db.execSQL("DROP TABLE IF EXISTS config");
//        onCreate(db);
        if (i == 1){
            ContentValues values = new ContentValues();
            values.put(Config.NAME, "timeApp");
            values.put(Config.VALUE, "66000");
            db.insert(CONFIG_TABLE_NAME, null, values);
        }
    }
}
