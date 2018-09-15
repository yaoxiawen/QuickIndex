package com.yxw.quickindex;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {
    private ListView mMainList;
    private ArrayList<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                //若有权限，继续操作
            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                Toast.makeText(MainActivity.this, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, 10);
            }
        }
        final QuickIndexBar bar = findViewById(R.id.bar);
        //设置监听
        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                // 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
                for (int i = 0; i < persons.size(); i++) {
                    String l = persons.get(i).getPinyin().charAt(0) + "";
                    if (TextUtils.equals(letter, l)) {
                        // 匹配成功，listview跳转
                        mMainList.setSelection(i);
                        break;
                    }
                }
            }
        });
        mMainList = findViewById(R.id.lv_main);
        persons = new ArrayList<>();
        // 填充数据 , 排序
        fillAndSortData(persons);
        mMainList.setAdapter(new MyAdapter(MainActivity.this, persons));
    }

    /**
     * 填充数据 , 排序
     */
    private void fillAndSortData(ArrayList<Person> persons) {
        // 填充数据
        for (int i = 0; i < Cheeses.NAMES.length; i++) {
            String name = Cheeses.NAMES[i];
            persons.add(new Person(name));
        }
        // 进行排序
        Collections.sort(persons);
    }
}
