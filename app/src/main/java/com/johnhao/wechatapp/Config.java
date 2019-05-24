package com.johnhao.wechatapp;

import android.net.Uri;
import android.provider.BaseColumns;

public class Config implements BaseColumns {

    // 构造方法
    private Config() {
    }

    public static final String AUTHORITY = "com.johnhao.wechat.provider";
    public static final String PATH_SINGLE = "config/#";
    public static final String PATH_MULTIPLE = "config";

    // 访问Uri
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_MULTIPLE);

    // 表字段常量
    public static final String KEY_ID = "_id";
    public static final String NAME = "name";                    // 值
    public static final String VALUE = "value";                // 内容


}
