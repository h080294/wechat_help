package com.johnhao.wechatapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Objects;

import static com.johnhao.wechatapp.Config.PATH_MULTIPLE;
import static com.johnhao.wechatapp.Config.PATH_SINGLE;

public class MyContentProvider extends ContentProvider {


    // 数据库帮助类
    private DatabaseHelper databaseHelper;
    public static final String CONFIG_TABLE_NAME = "setting";

    // Uri工具类
    private static final UriMatcher uriMatcher;

    // 查询、更新条件
    private static final int MULTIPLE = 1;
    private static final int SINGLE = 2;

    // 查询列集合
    private static HashMap<String, String> configMap;


    static {
        // Uri匹配工具类
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Config.AUTHORITY, PATH_MULTIPLE, MULTIPLE);
        uriMatcher.addURI(Config.AUTHORITY, PATH_SINGLE, SINGLE);

        // 实例化查询列集合
        configMap = new HashMap<String, String>();

        // 添加查询列
        configMap.put(Config.NAME, Config.NAME);
        configMap.put(Config.VALUE, Config.VALUE);
    }

    @Override
    public boolean onCreate() {
        // 实例化数据库帮助类
        databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db != null;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // 获得数据库实例
        SQLiteDatabase db = databaseHelper.getWritableDatabase();


        // 插入数据，返回行ID
//        long rowId = db.insertOrThrow(CONFIG_TABLE_NAME, null, values);
        long rowId = db.insertWithOnConflict(CONFIG_TABLE_NAME, Config._ID, values, SQLiteDatabase.CONFLICT_IGNORE);

        // 如果插入成功返回uri
        if (rowId > 0) {
            Uri empUri = ContentUris.withAppendedId(Config.CONTENT_URI, rowId);
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(empUri, null);
            return empUri;
        }
        throw new SQLException("failed to insert row into " + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CONFIG_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // 查询所有
            case MULTIPLE:
                qb.setProjectionMap(configMap);
                break;

//             根据ID查询
            case SINGLE:
                qb.setProjectionMap(configMap);
                qb.appendWhere(Config._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Uri错误！ " + uri);
        }

        // 获得数据库实例
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // 返回游标集合
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, null);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // 获得数据库实例
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // 获得数据库实例
        int count;
        switch (uriMatcher.match(uri)) {
            // 根据指定条件删除
            case MULTIPLE:
                count = db.delete(CONFIG_TABLE_NAME, selection, selectionArgs);
                break;
            // 根据指定条件和ID删除
            case SINGLE:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(CONFIG_TABLE_NAME, Config._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("错误的 URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // 获得数据库实例
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int count;

        switch (uriMatcher.match(uri)) {
            // 根据指定条件更新
            case MULTIPLE:
                count = db.update(CONFIG_TABLE_NAME, values, selection, selectionArgs);
                break;
            // 根据指定条件和ID更新
            case SINGLE:
                String segment = uri.getPathSegments().get(1);
                count = db.update(CONFIG_TABLE_NAME, values, Config.KEY_ID + "=" + segment, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("错误的 URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }
}
