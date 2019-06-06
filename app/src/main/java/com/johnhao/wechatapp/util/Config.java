package com.johnhao.wechatapp.util;

import android.net.Uri;
import android.provider.BaseColumns;

public class Config implements BaseColumns {

    // 构造方法
    private Config() {
    }

    public static final String AUTHORITY = "com.johnhao.wechat.provider";
    public static final String CONFIG_TABLE_NAME = "setting";
    public static final String DATA_TABLE_NAME = "data";

//    public static final String PATH_ID = "config/#";
    public static final String PATH = "config";

    // 访问Uri
    public static final Uri CONTENT_SETTING_URI = Uri.parse("content://" + AUTHORITY + "/" + CONFIG_TABLE_NAME);
    public static final Uri CONTENT_DATA_URI = Uri.parse("content://" + AUTHORITY + "/" + DATA_TABLE_NAME);


    // 表字段常量
    public static final String KEY_ID = "_id";
    public static final String NAME = "name";                    // 值
    public static final String VALUE = "value";                // 内容


}
