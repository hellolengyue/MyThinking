package com.hel.mythinking.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.JokeViewPagerAdapter;
import com.hel.mythinking.bean.NJ;
import com.hel.mythinking.utils.PopWindow;

import java.util.ArrayList;
import java.util.List;

public class JdShowActivity extends BaseActivity {
    private ViewPager viewPager;
    private List<NJ> datas = new ArrayList<>();
    private SQLiteDatabase database;
    private int type;
    private String title;
    private JokeViewPagerAdapter adapter;
    private Button back, more;
    private PopWindow popWindow;
    private TextView topTitle;
    private LinearLayout no_coll;

    @Override
    protected int getLayout() {
        return R.layout.activity_jd_show;
    }

    @Override
    protected void initView() {
        initAdData();
        back = (Button) findViewById(R.id.back);
        more = (Button) findViewById(R.id.more);
        topTitle = (TextView) findViewById(R.id.title);
        no_coll = (LinearLayout) findViewById(R.id.no_coll);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        title = intent.getStringExtra("title");
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (type == 0) {
            popWindow = new PopWindow(JdShowActivity.this, "收藏");
        } else {

            popWindow = new PopWindow(JdShowActivity.this, "取消收藏");
        }
        initData();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popWindow.showPopupWindow(more);
            }
        });
        popWindow.setOnItemClick(new PopWindow.ItemClick() {
            @Override
            public void itemClick(int res) {
                switch (res) {
                    case R.id.tv_more_share:
                        int currentItem = viewPager.getCurrentItem();
                        String currentContent = datas.get(currentItem).getProblem();
                        Intent textIntent = new Intent(Intent.ACTION_SEND);
                        if (currentContent.contains("、")) {
                            String[] split = currentContent.split("、");
                            currentContent = split[1];
                        }
                        textIntent.setType("text/plain");
                        textIntent.putExtra(Intent.EXTRA_TEXT, title+"：\n" + currentContent + "\n——更多经典语录尽在笑话急转弯");
                        startActivity(Intent.createChooser(textIntent, "分享"));
                        break;
                    case R.id.tv_more_coll:
                        if (type == 0) {
                            //收藏
                            ContentValues value = new ContentValues();
                            value.put("coll", 1);
                            int think = database.update("jingdian", value, "id = ?", new String[]{datas.get(viewPager.getCurrentItem()).getId() + ""});
                            if (think > 0) {
                                Toast.makeText(JdShowActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //取消收藏
                            ContentValues value = new ContentValues();
                            value.put("coll", 0);
                            int think = database.update("jingdian", value, "id = ?", new String[]{datas.get(viewPager.getCurrentItem()).getId() + ""});
                            datas.remove(viewPager.getCurrentItem());

                            if (datas.size() <= 0) {
                                topTitle.setText("经典语录");
                                no_coll.setVisibility(View.VISIBLE);
                                viewPager.setVisibility(View.GONE);
                                more.setVisibility(View.GONE);
                            } else {
                                adapter.notifyDataSetChanged();
                                int current = viewPager.getCurrentItem();
                                if (current + 1 > datas.size()) {
                                    current = datas.size() - 1;
                                }
                                topTitle.setText("经典语录(" + (current + 1) + "/" + datas.size() + ")");
                            }

                            if (think > 0) {
                                Toast.makeText(JdShowActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.tv_more_copy:
                        int currentItem3 = viewPager.getCurrentItem();
                        String currentContent3 = datas.get(currentItem3).getProblem();
                        if (currentContent3.contains("、")) {
                            String[] split = currentContent3.split("、");
                            currentContent3 = split[1];
                        }
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                        ClipData clipData = ClipData.newPlainText(null, currentContent3);
                        // 把数据集设置（复制）到剪贴板
                        cm.setPrimaryClip(clipData);
                        Toast.makeText(JdShowActivity.this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                topTitle.setText("经典语录(" + (position + 1) + "/" + datas.size() + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData() {
        database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        Cursor cursor;
        if (type == 0) {
            cursor = database.rawQuery("select * from jingdian where title = ? ", new String[]{title});
        } else {
            cursor = database.rawQuery("select * from jingdian where coll = ? ", new String[]{type + ""});
        }
        if (datas.size() > 0) {
            datas.clear();
        }
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("content"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            NJ nj = new NJ("", type, id);
            datas.add(nj);
        }
        if (datas.size() == 0) {
            topTitle.setText("经典语录");
            no_coll.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
        } else {
            adapter = new JokeViewPagerAdapter(JdShowActivity.this, datas);
            viewPager.setAdapter(adapter);
            topTitle.setText("经典语录(1/" + datas.size() + ")");
        }

    }

}
