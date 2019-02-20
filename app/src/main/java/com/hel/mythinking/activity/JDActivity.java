package com.hel.mythinking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.hel.mythinking.Constant;
import com.hel.mythinking.MyApplication;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.JdAdapter;
import com.hel.mythinking.utils.Utils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hel
 * @date 2018/8/8
 * 文件 MyThinking
 * 描述
 */

public class JDActivity extends BaseActivity {
    private List<String> indexNj = new ArrayList<>();
    private XRecyclerView jdRecyclerView;
    private JdAdapter jdAdapter;
    private int page = 0;
    private Dialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    jdRecyclerView.refreshComplete();
                    jdAdapter.notifyDataSetChanged();
                case 1:
                    dialog.dismiss();
                    jdAdapter.notifyDataSetChanged();
                    jdRecyclerView.loadMoreComplete();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_jd);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog = Utils.getLoading(JDActivity.this);
        dialog.show();
        initAdData();
        page = (int) MyApplication.sp.getSharedPreference("jdPage", 0);
        if (page >= 374) {
            page = 0;
        }
        jdRecyclerView = findViewById(R.id.jd_recycler_view);
        jdRecyclerView.setLayoutManager(new LinearLayoutManager(JDActivity.this));
        jdAdapter = new JdAdapter(indexNj);
        jdRecyclerView.setAdapter(jdAdapter);
        new Thread() {
            @Override
            public void run() {
                super.run();
                initData();
            }
        }.start();

        jdAdapter.setOnItemClickListener(new JdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = indexNj.get(position - 1);
                Intent intent = new Intent(JDActivity.this, JdShowActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("title", name);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        jdRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Collections.shuffle(indexNj);
                handler.sendEmptyMessageDelayed(0, 1500);
            }

            @Override
            public void onLoadMore() {
                if (page >= 374) {
                    page = 0;
                }
                initData();
            }
        });

    }


    private void initData() {
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        Cursor cursor = database.rawQuery("select distinct title from jingdian LIMIT 15 OFFSET " + page, null);
        List<String> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("title"));
            list.add(type);
        }
        Collections.shuffle(list);
        indexNj.addAll(list);
        cursor.close();
        page += 15;
        MyApplication.sp.put("jdPage", page);
        handler.sendEmptyMessage(1);

    }

}
