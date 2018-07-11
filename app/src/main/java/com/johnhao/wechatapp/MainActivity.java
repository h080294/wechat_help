package com.johnhao.wechatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private EditText editReplaceTarget,editReplaceText;
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

    private boolean isModuleActive(){
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
        if (isModuleActive()){
            Toast.makeText(this, "模块已启动", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "模块未启动", Toast.LENGTH_LONG).show();
        }
    }

}
