package com.johnhao.wechatapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.johnhao.wechatapp.adapter.DataAdapter;
import com.johnhao.wechatapp.adapter.TextContent;
import com.johnhao.wechatapp.util.Config;
import java.util.ArrayList;
import java.util.List;

import static com.johnhao.wechatapp.util.Config.CONTENT_DATA_URI;
import static com.johnhao.wechatapp.util.Config.CONTENT_SETTING_URI;
import static com.johnhao.wechatapp.util.Utils.getWXVerName;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "hookwechat";
    private SharedPreferences prefs;
    private EditText editReplaceTarget, editReplaceText;
    private CheckBox checkbox, wechatLog;
    private SharedPreferences.Editor editor;
    private ContentResolver resolver;
    private String weixinVersion;
    private List<TextContent> list = new ArrayList<>();
    private DataAdapter dataAdapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initData();
        initViews();
        checkActive();
        checkWeixinVersion();
    }

    private boolean isModuleActive() {
        return false;
    }

    private void checkActive() {
        if (isModuleActive()) {
            Toast.makeText(this, "模块已启动", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "模块未启动", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        editReplaceTarget = findViewById(R.id.replace_target);
        editReplaceText = findViewById(R.id.replace_text);
        Button save = findViewById(R.id.btn_save);
        checkbox = findViewById(R.id.ischecked);
        wechatLog = findViewById(R.id.wechectlog);
        checkbox.setOnClickListener(this);
        wechatLog.setOnClickListener(this);
        checkbox.setChecked(prefs.getString("isActive", "no").equalsIgnoreCase("yes"));
        wechatLog.setChecked(prefs.getString("logOpen", "no").equals("yes"));
        save.setOnClickListener(this);

        RecyclerView rv = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(this, list);
        rv.setAdapter(dataAdapter);

    }

    private void init() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        resolver = this.getContentResolver();
        editor = prefs.edit();
        weixinVersion = getWXVerName(this);

        if (prefs.getInt("new", 0) != 0) {
            return;
        }

        // 首次安装，写入默认数据
        editor.putInt("new", 1);
        editor.putString("isActive", "no");
        editor.putString("logOpen", "no");
        editor.putString("weixinVersion", weixinVersion);
        editor.apply();

        // 初始化设置
        initSetting();
    }

    private void initSetting() {
        ContentValues values = new ContentValues();
        values.put(Config.NAME, "isActive");
        values.put(Config.VALUE, "no");
        resolver.insert(CONTENT_SETTING_URI, values);
        values.clear();

        values.put(Config.NAME, "logOpen");
        values.put(Config.VALUE, "no");
        resolver.insert(CONTENT_SETTING_URI, values);
        values.clear();

        String weixinVersion = getWXVerName(this);
        values.put(Config.NAME, "weixinVersion");
        values.put(Config.VALUE, weixinVersion);
        resolver.insert(CONTENT_SETTING_URI, values);
        values.clear();

    }

    private void checkWeixinVersion() {

        if (weixinVersion.equals(prefs.getString("weixinVersion", ""))) {
            return;
        }

        updateSetting("weixinVersion", weixinVersion);

    }

    private void initData() {

        Cursor cursor = resolver.query(CONTENT_DATA_URI, new String[]{"name", "value"}, null, null, null);

        if (cursor == null) {
            Log.d(TAG, "数据库中没有数据 ");
            return;
        }

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                TextContent textContent = new TextContent(name, value);
                list.add(textContent);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if (list.size() == 0) {
            TextContent textContent = new TextContent("示例：被替换内容", "示例：要替换内容");
            insertData("示例：被替换内容", "示例：要替换内容");
            list.add(textContent);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ischecked:
                if (!checkbox.isChecked()) {
                    editor.putString("isActive", "no");
                    updateSetting("isActive", "no");
                } else {
                    editor.putString("isActive", "yes");
                    updateSetting("isActive", "yes");
                }
                editor.apply();
                break;
            case R.id.wechectlog:
                if (!wechatLog.isChecked()) {
                    editor.putString("logOpen", "no");
                    updateSetting("logOpen", "no");
                } else {
                    editor.putString("logOpen", "yes");
                    updateSetting("logOpen", "yes");
                }
                editor.apply();
                break;
            case R.id.btn_update:
                // do clear
                // textview
                // 删db
                String replaceTarget = editReplaceTarget.getText().toString();
                String replaceText = editReplaceText.getText().toString();

                if (TextUtils.isEmpty(replaceTarget) || TextUtils.isEmpty(replaceText)) {
                    Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 更新db
                insertData(replaceTarget, replaceText);

                //清空view
                editReplaceTarget.setText("");
                editReplaceText.setText("");

                // 更新adapter
                TextContent textContent = new TextContent(replaceTarget,replaceText);
                dataAdapter.addData(0, textContent);

                Toast.makeText(this, "更新了数据" + position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_save:
                replaceTarget = editReplaceTarget.getText().toString();
                replaceText = editReplaceText.getText().toString();

                if (TextUtils.isEmpty(replaceTarget) || TextUtils.isEmpty(replaceText)) {
                    Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 更新db
                insertData(replaceTarget, replaceText);

                //清空view
                editReplaceTarget.setText("");
                editReplaceText.setText("");

                // 更新adapter
                TextContent textContent1 = new TextContent(replaceTarget,replaceText);
                dataAdapter.addData(0, textContent1);

                Toast.makeText(this, "插入了数据", Toast.LENGTH_SHORT).show();

                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    private void updateSetting(String name, String value) {
        ContentValues values = new ContentValues();
        values.put(Config.NAME, name);
        values.put(Config.VALUE, value);
        String[] selectValue = {name};
        resolver.update(CONTENT_SETTING_URI, values, "name=?", selectValue);
        values.clear();
    }

    public void updataData(String name, String value) {
        ContentValues values = new ContentValues();
        values.put(Config.NAME, name);
        values.put(Config.VALUE, value);
        String[] selectValue = {name};
        resolver.update(CONTENT_DATA_URI, values, "name=?", selectValue);
    }

    private void insertData(String name, String value) {
        ContentValues values = new ContentValues();
        values.put(Config.NAME, name);
        values.put(Config.VALUE, value);
        resolver.insert(CONTENT_DATA_URI, values);
        values.clear();
    }

    public void deleteData(String name) {
        String[] selectValue = {name};
        resolver.delete(CONTENT_DATA_URI, "name=?", selectValue);
    }

    public void setInputText(String target, String replcae, int position) {
        this.position = position;

        editReplaceTarget.setText(target);
        editReplaceText.setText(replcae);
    }

}