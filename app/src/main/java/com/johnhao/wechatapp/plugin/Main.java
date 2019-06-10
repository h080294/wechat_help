package com.johnhao.wechatapp.plugin;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.johnhao.wechatapp.MyApplication;
import com.johnhao.wechatapp.util.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by johnhao on 2018/7/5.
 */

public class Main implements IXposedHookLoadPackage {

    private static final String TAG = "hookwechat";
    private final static String ERROR_TAG = "hookwechat.Error";
    private final static String INJECT_TAG = "hookwechat.Inject";
    private final static String JS_TAG = "hookwechat.jsLog";
    private final static String WX_TAG = "hookwechat.wxLog";
    private boolean isLogcatOpen;
    private boolean active;
    private String weixinVersion;
    private static WeixinVerBase weixin;
    private Map<String, String> settings = new HashMap<>();
    private Map<String, String> replaceData = new HashMap<>();
    private XC_LoadPackage.LoadPackageParam mLoadPackageParam;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String MY_APP_MAINACTIVITY = "com.johnhao.wechatapp.MainActivity";

        // Xposed模块自检测
        if (loadPackageParam.packageName.equals("com.johnhao.wechatapp")) {
            Log.d(TAG, "模块自检侧");
            XposedHelpers.findAndHookMethod(MY_APP_MAINACTIVITY, loadPackageParam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }


        // hook代码
        if (!loadPackageParam.packageName.equals("com.tencent.mm")) {
            return;
        }
        Log.d(TAG, "准备开始hook微信");

        mLoadPackageParam = loadPackageParam;


        final Map<String,String> innerMap = new HashMap<>();

        // hook代码
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                ClassLoader loader = context.getClassLoader();

                getSettings(context);
                getReplaceText(context);


                Log.d(TAG, "Settings：\nisActive: " + active + "\nisLogcatOpen: " + isLogcatOpen + "\nweixinVerson " + weixinVersion);
                Log.d(TAG, "Replace Data: \n" + settings);

                if (TextUtils.isEmpty(weixinVersion)) {
                    Log.d(TAG, "默认使用7.0.4版本");
                    weixin = new WeixinVer7_0_4();
                } else {
                    //
                    Log.d(TAG, "7.0.4");
                    weixin = new WeixinVer7_0_4();
                }


                /**
                 *  hook 小程序菜单
                 */

                String name = param.method.getDeclaringClass().getName() + "." + param.method.getName();
                if (name.equals(weixin.ABI_CLS_APPBRAND_INIT_CONFIG + "." + weixin.ABI_SIMPLE_FUN_APPBRAND_INIT_CONFIG)){
                    // 只要在这里修改掉传入的AppBrandSysConfig即可打开调试功能
                    Class<?> appBrandSysConfigClass = param.method.getDeclaringClass().getClassLoader().loadClass(weixin.ABI_CLS_APPBRAND_APPBRANDSYSCONFIG);
                    Object arg0 = param.args[0];
                    if (arg0 == null) {
                        Log.d(TAG, "传入的AppBrandSysConfig为空");
                    }

                } else if (name.equals(weixin.ABI_CLS_APPBRAND_MENU_DEBUG + "." + weixin.ABI_SIMPLE_FUN_APPBRAND_MENU_ADD)) {

                    Class<?> nClass = param.method.getDeclaringClass().getClassLoader().loadClass(weixin.ABI_CLS_APPBRAND_MENU_WIDGET_BASE);
                    Method fMethod = nClass.getDeclaredMethod(weixin.ABI_SIMPLE_FUN_APPBRAND_MENU_ADD_WIDGET_BASE, int.class, CharSequence.class);
                    fMethod.setAccessible(true);
                    Object arg2 = param.args[2];
                    fMethod.invoke(arg2, 3, "开启/关闭调试");
                    param.setResult(null);
                }


                    /**
                     *  替换game.js
                     */
                if (active) {

                    final Map<String,String> _map = settings;
                    Log.d(TAG, "hookGameJs method: " + weixin.WX_CLS_APPCACHE + "." + weixin.WX_METHOD_APPCACHE);

                    XposedBridge.hookAllMethods(XposedHelpers.findClass(weixin.WX_CLS_APPCACHE, loader), weixin.WX_METHOD_APPCACHE, new XC_MethodHook() {
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                            Log.d(TAG, "准备执行hookGameJs ");

                            if (methodHookParam.args.length >= 2 && ((String) methodHookParam.args[1]).equals("game.js")) {
                                String obj = (String) methodHookParam.getResult();
//                        saveFile(obj, "game.txt");
                                Utils.writeFileToSDCard(obj.getBytes(), null, "game.txt", false, false);
                                Log.d(TAG, "开始替换逻辑");

                                for (String key : _map.keySet()) {
                                    obj = obj.replace(key, _map.get(key));
                                }

                                Utils.writeFileToSDCard(obj.getBytes(), null, "gamefixed.txt", false, false);
//                        saveFile(obj, "gamefixed.txt");
                                methodHookParam.setResult(obj);
                                Log.d(TAG, "Hooked End");
                            }
                        }

                        protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        }
                    });

                }

                /**
                 *  Hook log
                 */

                if (isLogcatOpen) {
                    Log.d(TAG, "准备执行hookLog ");

                    XposedHelpers.findAndHookMethod(weixin.ABI_CLS_APPBRAND_APPBRANDSYSCONFIG, loader, weixin.ABI_METHOD_APPBRANDSYSCONFIG_STRING_TOSTRING, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String result = (String) param.getResult();
                            Log.d(TAG, "AppBrandSysConfig: " + result);
                        }
                    });

                    XC_MethodHook logCallback = new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            boolean log = false;
                            Throwable ex = new Throwable();
                            StackTraceElement[] elements = ex.getStackTrace();
                            for (StackTraceElement element : elements) {
                                if (element.getClassName().contains("com.tencent.mm.plugin.appbrand")) {
                                    log = true;
                                    break;
                                }
                            }
                            if (!log) {
                                return;
                            }
                            int level = -1;
                            String name = param.method.getName();
                            String arg0 = (String) param.args[0];
                            String arg1 = (String) param.args[1];
                            Object[] arg2 = (Object[]) param.args[2];
                            String format = arg2 == null ? arg1 : String.format(arg1, arg2);
                            if (TextUtils.isEmpty(format)) {
                                format = "null";
                            }
                            if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_F) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_I)) {
                                level = 0;
                            } else if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_D) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_V) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_K) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_L)) {
                                level = 1;
                            } else if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_E) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_W)) {
                                level = 2;
                            }
                            switch (level) {
                                case 0:
                                    Log.i(WX_TAG + " " + arg0, format);
                                    break;
                                case 1:
                                    Log.d(WX_TAG + " " + arg0, format);
                                    break;
                                case 2:
                                    Log.e(WX_TAG + " " + arg0, format);
                                    break;
                            }
                        }
                    };

                    Class<?> logClass = loader.loadClass(weixin.WXLOG_CLS_PLATFORMTOOLS_LOG);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_F, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_E, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_W, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_I, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_D, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_V, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_K, String.class, String.class, Object[].class, logCallback);
                    XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_L, String.class, String.class, Object[].class, logCallback);

                    // 将小程序日志自定义转发到java
                    Class<?> arg0Class = loader.loadClass(weixin.ABLOG_CLS_JSAPI_PARMA0);
                    XposedHelpers.findAndHookMethod(weixin.ABLOG_CLS_JSAPI_LOG, loader, weixin.ABLOG_SIMPLE_FUN_JSAPI_LOG, arg0Class, JSONObject.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            JSONObject jsonObjectArg1 = (JSONObject) param.args[1];
                            int l = jsonObjectArg1.getInt("level");
                            String logs = jsonObjectArg1.getString("logs");
                            switch (l) {
                                case 0:
                                    Log.d(JS_TAG, logs);
                                    break;
                                case 1:
                                    Log.i(JS_TAG, logs);
                                    break;
                                case 2:
                                    Log.w(JS_TAG, logs);
                                    break;
                                case 3:
                                    Log.e(JS_TAG, logs);
                                    break;
                            }
                        }
                    });

                }




            }
        });



    }

    public void hookGameJs(ClassLoader loader, final Map<String, String> maps) {
        Log.d(TAG, "hookGameJs: Start");

        XposedBridge.hookAllMethods(XposedHelpers.findClass(weixin.WX_CLS_APPCACHE, loader), weixin.WX_METHOD_APPCACHE, new XC_MethodHook() {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {

                if (methodHookParam.args.length >= 2 && ((String) methodHookParam.args[1]).equals("game.js")) {
                    String obj = (String) methodHookParam.getResult();
//                        saveFile(obj, "game.txt");
                    Utils.writeFileToSDCard(obj.getBytes(), null, "game.txt", false, false);
                    Log.d(TAG, "开始替换逻辑");

                    for (String key : maps.keySet()) {
                        obj = obj.replace(key, maps.get(key));
                    }

                    Utils.writeFileToSDCard(obj.getBytes(), null, "gamefixed.txt", false, false);
//                        saveFile(obj, "gamefixed.txt");
                    methodHookParam.setResult(obj);
                    Log.d(TAG, "Hooked End");
                }
            }

            protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            }
        });
    }

    public void hookLog(ClassLoader loader) throws ClassNotFoundException {
        Log.d(TAG, "hookLog Start");

        XC_MethodHook logCallback = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean log = false;
                Throwable ex = new Throwable();
                StackTraceElement[] elements = ex.getStackTrace();
                for (StackTraceElement element : elements) {
                    if (element.getClassName().contains("com.tencent.mm.plugin.appbrand")) {
                        log = true;
                        break;
                    }
                }
                if (!log) {
                    return;
                }
                int level = -1;
                String name = param.method.getName();
                String arg0 = (String) param.args[0];
                String arg1 = (String) param.args[1];
                Object[] arg2 = (Object[]) param.args[2];
                String format = arg2 == null ? arg1 : String.format(arg1, arg2);
                if (TextUtils.isEmpty(format)) {
                    format = "null";
                }
                if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_F) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_I)) {
                    level = 0;
                } else if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_D) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_V) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_K) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_L)) {
                    level = 1;
                } else if (name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_E) || name.equals(weixin.WXLOG_SIMPLE_FUN_LOG_W)) {
                    level = 2;
                }
                switch (level) {
                    case 0:
                        Log.i(WX_TAG + " " + arg0, format);
                        break;
                    case 1:
                        Log.d(WX_TAG + " " + arg0, format);
                        break;
                    case 2:
                        Log.e(WX_TAG + " " + arg0, format);
                        break;
                }
            }
        };
        Class<?> logClass = loader.loadClass(weixin.WXLOG_CLS_PLATFORMTOOLS_LOG);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_F, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_E, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_W, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_I, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_D, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_V, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_K, String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, weixin.WXLOG_SIMPLE_FUN_LOG_L, String.class, String.class, Object[].class, logCallback);

        // 将小程序日志自定义转发到java
        Class<?> arg0Class = loader.loadClass(weixin.ABLOG_CLS_JSAPI_PARMA0);
        XposedHelpers.findAndHookMethod(weixin.ABLOG_CLS_JSAPI_LOG, loader, weixin.ABLOG_SIMPLE_FUN_JSAPI_LOG, arg0Class, JSONObject.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                JSONObject jsonObjectArg1 = (JSONObject) param.args[1];
                int l = jsonObjectArg1.getInt("level");
                String logs = jsonObjectArg1.getString("logs");
                switch (l) {
                    case 0:
                        Log.d(JS_TAG, logs);
                        break;
                    case 1:
                        Log.i(JS_TAG, logs);
                        break;
                    case 2:
                        Log.w(JS_TAG, logs);
                        break;
                    case 3:
                        Log.e(JS_TAG, logs);
                        break;
                }
            }
        });
    }

    /**
     * 在微信小游戏菜单中添加菜单项
     *
     * @param param 方法有关的参赛
     * @param id    菜单项对应的id,应该跟排序有关
     * @param name  菜单显示的名字
     * @throws Throwable
     */
    private void hookAppBrandMenu(XC_MethodHook.MethodHookParam param, int id, String name) throws Throwable {
        Log.d(INJECT_TAG, "Hook 小程序菜单");
        Class<?> nClass = param.method.getDeclaringClass().getClassLoader().loadClass(weixin.ABI_CLS_APPBRAND_MENU_WIDGET_BASE);
        Method fMethod = nClass.getDeclaredMethod(weixin.ABI_SIMPLE_FUN_APPBRAND_MENU_ADD_WIDGET_BASE, int.class, CharSequence.class);
        fMethod.setAccessible(true);
        Object arg2 = param.args[2];
        fMethod.invoke(arg2, id, name);
        param.setResult(null);
    }


    private void getSettings(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.johnhao.wechat.provider/setting");

        Cursor cursor = resolver.query(uri, new String[]{"name", "value"}, null, null, null);

        if (cursor == null) {
            Log.d(TAG, "数据库中没有数据 ");
            return;
        }

        Log.d(TAG, "setting数据库：" + cursor.getCount() + "条记录");

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("name")).contains("isActive")) {
                    active = cursor.getString(cursor.getColumnIndex("value")).equalsIgnoreCase("yes");
//                    settings.put(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("value")));
                } else if (cursor.getString(cursor.getColumnIndex("name")).contains("logOpen")) {
                    isLogcatOpen = cursor.getString(cursor.getColumnIndex("value")).equalsIgnoreCase("yes");
//                    settings.put(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("value")));
                } else if (cursor.getString(cursor.getColumnIndex("name")).contains("weixinVersion")) {
                    weixinVersion = cursor.getString(cursor.getColumnIndex("value"));
//                    settings.put(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("value")));
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

    }

    private void getReplaceText(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.johnhao.wechat.provider/data");

        Cursor cursor = resolver.query(uri, new String[]{"name", "value"}, null, null, null);

        if (cursor == null) {
            Log.d(TAG, "数据库中没有数据 ");
            return;
        }

        Log.d(TAG, "data数据库：" + cursor.getCount() + "条记录");

        if (cursor.moveToFirst()) {
            do {
                replaceData.put(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("value")));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

}

