package com.johnhao.wechatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    private String isLogcatOpen;
    private String active;
    private String replace;
    private String replace_text;


    public static void saveFile(String str, String str2) {
        try {
            File file = new File(Environment.getExternalStorageState().equals("mounted") ? Environment.getExternalStorageDirectory().toString() + File.separator + str2 : Environment.getDownloadCacheDirectory().toString() + File.separator + str2);
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        // Xposed模块自检测
        if (loadPackageParam.packageName.equals("com.johnhao.wechatapp")) {
            XposedHelpers.findAndHookMethod("com.johnhao.wechatapp.MainActivity", loadPackageParam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
            Log.d(TAG, "自检测");
        }


        // hook代码
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            Log.d(TAG, "准备开始hook微信");

            getConfig();

            boolean is_Actived = active.equalsIgnoreCase("yes");
            boolean is_LogcatOpen = isLogcatOpen.equalsIgnoreCase("yes");

            if (is_Actived) {
                Log.d(TAG, "获取信息  Active：yes");

                XposedBridge.hookAllMethods(XposedHelpers.findClass("com.tencent.mm.plugin.appbrand.appcache.ao", loadPackageParam.classLoader), "a", new XC_MethodHook() {
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {

                        if (methodHookParam.args.length >= 2 && ((String) methodHookParam.args[1]).equals("game.js")) {
                            Log.d(TAG, "afterHookedMethod: 开始hook微信小程序");
                            String obj = (String) methodHookParam.getResult();
                            saveFile(obj, "game.txt");
                            Log.d(TAG, "开始替换逻辑: " + "\n before fix: " + replace + "\n after fix: " + replace_text);
                            obj = obj.replace(replace, replace_text);
                            saveFile(obj, "gamefixed.txt");
                            methodHookParam.setResult(obj);
                            Log.d(TAG, "Hooked End");
                        }
                    }

                    protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    }
                });

            }

            // 是否打开微信Log开关
            // 微信versionCode='1321' versionName='6.6.7'
            if (is_LogcatOpen) {
                Log.d(TAG, "获取信息  logOpen： yes");

                //public static void keep_setupXLog(boolean z, String str, String str2, Integer num, Boolean bool, Boolean bool2, String str3)
                XposedHelpers.findAndHookMethod("com.tencent.mm.xlog.app.XLogSetup", loadPackageParam.classLoader, "keep_setupXLog",
                        boolean.class, String.class, String.class, Integer.class, Boolean.class,
                        Boolean.class, //isLogcatOpen
                        String.class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.args[5] = true;
                                //param.setResult(null);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                param.args[5] = true;
                                super.afterHookedMethod(param);
                                Log.i(TAG, "keep_setupXLog参数isLogcatOpen: " + param.args[5]);
                            }
                        });

                //platformtools_x
                // Log f
                //public static void f(String str, String str2, Object... objArr)
                XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "f",
                        String.class, String.class, Object[].class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String) param.args[0];
                                String str2 = (String) param.args[1];
                                Object[] objArr = (Object[]) param.args[2];
                                String format = objArr == null ? str2 : String.format(str2, objArr);
                                Log.e(TAG + "f" + str, format);
                                super.beforeHookedMethod(param);
                            }
                        });

                //platformtools_x
                // Log e
                XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "e",
                        String.class, String.class, Object[].class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String) param.args[0];
                                String str2 = (String) param.args[1];
                                Object[] objArr = (Object[]) param.args[2];
                                String format = objArr == null ? str2 : String.format(str2, objArr);
                                if (format == null) {
                                    format = "";
                                }

                                Log.e(TAG + "e" + str, format);

                                super.beforeHookedMethod(param);
                            }
                        });

                //platformtools_x
                // Log w
                XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "w",
                        String.class, String.class, Object[].class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String) param.args[0];
                                String str2 = (String) param.args[1];
                                Object[] objArr = (Object[]) param.args[2];
                                String format = objArr == null ? str2 : String.format(str2, objArr);
                                if (format == null) {
                                    format = "";
                                }

                                Log.e(TAG + "w" + str, format);

                                super.beforeHookedMethod(param);
                            }
                        });

                //platformtools_x
                // Log v
                XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "v",
                        String.class, String.class, Object[].class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String) param.args[0];
                                String str2 = (String) param.args[1];
                                Object[] objArr = (Object[]) param.args[2];
                                String format = objArr == null ? str2 : String.format(str2, objArr);
                                if (format == null) {
                                    format = "";
                                }

                                Log.e(TAG + "v" + str, format);

                                super.beforeHookedMethod(param);
                            }
                        });

                //platformtools_x
                // Log i
                XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", loadPackageParam.classLoader, "i",
                        String.class, String.class, Object[].class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String) param.args[0];
                                String str2 = (String) param.args[1];
                                Object[] objArr = (Object[]) param.args[2];
                                String format = objArr == null ? str2 : String.format(str2, objArr);
                                if (format == null) {
                                    format = "";
                                }

                                Log.e(TAG + "i" + str, format);

                                super.beforeHookedMethod(param);
                            }
                        });
            }
        }

        final Context context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", (ClassLoader) null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);

    }

    private void getConfig() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/config.txt";

        final File file = new File(path);
        if (file.isDirectory()) {
            Log.d(TAG, "getConfig: File [/sdcard/config.txt] not exist");
        } else {

            try {
                InputStream instream = new FileInputStream(file);
                InputStreamReader inputReader = new InputStreamReader(instream);
                BufferedReader bufferedReader = new BufferedReader(inputReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("isActive")) {
                        active = line.substring(line.indexOf(":") + 1);
                    } else if (line.contains("logOpen")) {
                        isLogcatOpen = line.substring(line.indexOf(":") + 1);
                    } else if (line.contains("replace_target")) {
                        replace = line.substring(line.indexOf(":") + 1);
                    } else if (line.contains("replace_text")) {
                        replace_text = line.substring(line.indexOf(":") + 1);
                    }
                }
                instream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

