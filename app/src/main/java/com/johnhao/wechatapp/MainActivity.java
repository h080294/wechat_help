package com.johnhao.wechatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private EditText editReplaceTarget;
    private EditText editReplaceText;
    private Button save;
    private CheckBox checkbox;
    private CheckBox wechatLog;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isModuleActive()){
            Toast.makeText(this, "模块已启动", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "模块未启动", Toast.LENGTH_LONG).show();
        }

        editReplaceTarget = findViewById(R.id.replace_target);
        editReplaceText = findViewById(R.id.replace_text);
        save = findViewById(R.id.btn_save);
        checkbox = findViewById(R.id.ischecked);
        wechatLog = findViewById(R.id.wechectlog);


        final boolean isCheck = prefs.getBoolean("is_checked", true);
        boolean iswechecklog = prefs.getBoolean("is_wechat_log", false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        wechatLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("is_wechat_log", true);

                } else {
                    editor.putBoolean("is_wechat_log", false);
                }
                editor.apply();
            }
        });

        if (isCheck) {
            checkbox.setChecked(true);
        }

        if (iswechecklog) {
            wechatLog.setChecked(true);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String replace_target_text = editReplaceTarget.getText().toString();
                String replace_text = editReplaceText.getText().toString();


                if (!checkbox.isChecked()) {
                    replace_target_text = "this.score+=t";
                    replace_text = "this.score+=t+=10";
                    editor.putString("replace_target_text", replace_target_text);
                    editor.putString("replace_text", replace_text);
                    editor.putBoolean("is_checked", false);
                } else {
                    editor.putString("replace_target_text", replace_target_text);
                    editor.putString("replace_text", replace_text);
                    editor.putBoolean("is_checked", true);
                }
                editor.apply();
                getKey();
            }
        });

    }

    private boolean isModuleActive(){
        return false;
    }

    private void getKey() {
        Intent intent = new Intent("com.johnhao.wechatapp.SETTING_CHANGED");
        intent.putExtra("replace_target_text", prefs.getString("replace_target_text", ""));
        intent.putExtra("replace_text", prefs.getString("replace_text", ""));
        intent.putExtra("is_wechat_log", prefs.getBoolean("is_wechat_log", false));
        sendBroadcast(intent);
    }

}
