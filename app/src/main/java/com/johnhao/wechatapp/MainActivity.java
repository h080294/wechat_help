package com.johnhao.wechatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "hookwechat";
    private SharedPreferences prefs;
    private EditText editReplaceTarget, editReplaceText;
    private Button save;
    private CheckBox checkbox, wechatLog;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkActive();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        if (prefs.getInt("new", 0) == 0) {
            initPref();
        }

        checkbox.setChecked(prefs.getString("isActive", "yes").equalsIgnoreCase("yes"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkbox.isChecked()) {
                    Toast.makeText(MainActivity.this, "激活才能正常使用", Toast.LENGTH_SHORT).show();
                    return;
                }

                String replaceTarget = editReplaceTarget.getText().toString();
                String replaceText = editReplaceText.getText().toString();

                if (TextUtils.isEmpty(replaceTarget) || TextUtils.isEmpty(replaceText)) {
                    Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                editor.putString("replace_target", replaceTarget);
                editor.putString("replace_text", replaceText);
                editor.apply();

                Intent intent = new Intent("com.johnhao.wechatapp.SETTING_CHANGED");
                intent.putExtra("replace_target", replaceTarget);
                intent.putExtra("replace_text", replaceText);
                intent.putExtra("isActive", prefs.getString("isActive", "yes"));
                intent.putExtra("logOpen", prefs.getString("logOpen", "no"));
                sendBroadcast(intent);

                String isActive = "isActive:" + prefs.getString("isActive", "yes") + "\r\n";
                String logOpen = "logOpen:" + prefs.getString("logOpen", "no") + "\r\n";
                String replace_target = "replace_target:" + replaceTarget + "\r\n";
                String replace_text = "replace_text:" + replaceText + "\r\n";
                String config = isActive + logOpen + replace_target + replace_text;
//                saveConfig(config);
                writeFileToSDCard(config.getBytes(), null, "config.txt", false, false);
                Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("isActive", "yes");

                } else {
                    editor.putString("isActive", "no");
                }
                editor.apply();
            }
        });

        wechatLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editor.putString("logOpen", "yes");

                } else {
                    editor.putString("logOpen", "no");
                }
                editor.apply();
            }
        });

    }

    private boolean isModuleActive() {
        return false;
    }

    private void initViews() {
        editReplaceTarget = findViewById(R.id.replace_target);
        editReplaceText = findViewById(R.id.replace_text);
        save = findViewById(R.id.btn_save);
        checkbox = findViewById(R.id.ischecked);
        wechatLog = findViewById(R.id.wechectlog);
    }

    private void initPref() {
        // 首次安装，写入默认数据
        editor.putInt("new", 1);
        editor.putString("isActive", "yes");
        editor.putString("logOpen", "no");
        editor.putString("replace_target", "this.diamonds-t");
        editor.putString("replace_text", "this.diamonds-t");
        checkbox.setChecked(true);
    }

    private void checkActive() {
        if (isModuleActive()) {
            Toast.makeText(this, "模块已启动", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "模块未启动", Toast.LENGTH_LONG).show();
        }
    }


    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /* 此方法为android程序写入sd文件文件，用到了android-annotation的支持库@
    * @param buffer   写入文件的内容
    * @param folder   保存文件的文件夹名称,如log；可为null，默认保存在sd卡根目录
    * @param fileName 文件名称，默认app_log.txt
    * @param append   是否追加写入，true为追加写入，false为重写文件
    * @param autoLine 针对追加模式，true为增加时换行，false为增加时不换行
    */
    public synchronized static void writeFileToSDCard(@Nullable final byte[] buffer, @Nullable final String folder,
                                                      @Nullable final String fileName, final boolean append, final boolean autoLine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean sdCardExist = Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED);
                String folderPath = "";
                if (sdCardExist) {
                    //TextUtils为android自带的帮助类
                    if (TextUtils.isEmpty(folder)) {
                        //如果folder为空，则直接保存在sd卡的根目录
                        folderPath = Environment.getExternalStorageDirectory()
                                + File.separator;
                    } else {
                        folderPath = Environment.getExternalStorageDirectory()
                                + File.separator + folder + File.separator;
                    }
                } else {
                    return;
                }

                File fileDir = new File(folderPath);
                if (!fileDir.exists()) {
                    if (!fileDir.mkdirs()) {
                        return;
                    }
                }
                File file;

                //判断文件名是否为空
                if (TextUtils.isEmpty(fileName)) {
                    file = new File(folderPath + "app_log.txt");
                } else {
                    file = new File(folderPath + fileName);
                }
                RandomAccessFile raf = null;
                FileOutputStream out = null;
                try {
                    if (append) {
                        //如果为追加则在原来的基础上继续写文件
                        raf = new RandomAccessFile(file, "rw");
                        raf.seek(file.length());
                        raf.write(buffer);
                        if (autoLine) {
                            raf.write("\n".getBytes());
                        }
                    } else {
                        //重写文件，覆盖掉原来的数据
                        out = new FileOutputStream(file);
                        out.write(buffer);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}