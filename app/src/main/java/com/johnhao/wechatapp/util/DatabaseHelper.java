package com.johnhao.wechatapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "config.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CONFIG_TABLE_NAME = "setting";
    private static final String DATA_TABLE_NAME = "data";

    private static final String CREATE_CONFIG = "create table if not exists " + CONFIG_TABLE_NAME + "("
            + Config._ID + " INTEGER PRIMARY KEY,"
            + "name text, "
            + "value text )";

    private static final String DATA_NAME = "create table if not exists " + DATA_TABLE_NAME + "("
            + Config._ID + " INTEGER PRIMARY KEY,"
            + "name text, "
            + "value text )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONFIG);
        db.execSQL(DATA_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // 删除表
//        db.execSQL("DROP TABLE IF EXISTS config");
//        onCreate(db);

    }
}
