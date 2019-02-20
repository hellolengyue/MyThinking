package com.hel.mythinking.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.bean.NJ;
import com.hel.mythinking.utils.PopWindow;
import com.hel.mythinking.utils.Utils;
import com.sixth.adwoad.AdListener;
import com.sixth.adwoad.AdwoAdView;
import com.sixth.adwoad.ErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hel
 * @date 2018/3/2
 * 文件 MyThinking
 * 描述
 */

public class ThinkActivity extends BaseActivity implements View.OnClickListener {

    private List<NJ> njDatas = new ArrayList<>();
    private Button back,more;
    private TextView title, ask, answer, next, pre;
    private LinearLayout  no_coll;
    private RelativeLayout ll_contorl;
    public static RelativeLayout layout;
    static AdwoAdView adView = null;
    RelativeLayout.LayoutParams params = null;
    private int count = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            if (njDatas.size() > 0) {
                title.setText(titleStr+"("+count+"/"+njDatas.size()+")");
                ask.setText(njDatas.get(count - 1).getProblem());
            } else {
                ll_contorl.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
                title.setText(R.string.title_think);
                ask.setVisibility(View.GONE);
                answer.setVisibility(View.GONE);
                no_coll.setVisibility(View.VISIBLE);
            }
        }
    };
    private SQLiteDatabase database;
    private int type;
    private Dialog dialog;
    private PopWindow popWindow;
    private String titleStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_think);
        initAdData();
        database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        back = findViewById(R.id.back);
        more = findViewById(R.id.more);
        title = findViewById(R.id.title);
        ask = findViewById(R.id.ask);
        answer = findViewById(R.id.answer);
        pre = findViewById(R.id.pre);
        next = findViewById(R.id.next);
        ll_contorl = findViewById(R.id.ll_contorl);
        no_coll = findViewById(R.id.no_coll);



        back.setOnClickListener(this);
        answer.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        layout = findViewById(R.id.layout);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		当不设置广告条充满屏幕宽时建议放置在父容器中间
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // 实例化广告对象
        adView = new AdwoAdView(this, Adwo_PID, false, 20);
        // 设置广告监听回调
        adView.setListener(new AdListener() {
            @Override
            public void onReceiveAd(Object arg0) {
            }

            @Override
            public void onFailedToReceiveAd(View adView, ErrorCode errorCode) {
            }


            @Override
            public void onDismissScreen() {
            }

            @Override
            public void onPresentScreen() {
            }
        });
        layout.addView(adView, params);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        titleStr = intent.getStringExtra("title");
        if (type == 0) {
            popWindow = new PopWindow(ThinkActivity.this, "收藏");
        } else {

            popWindow = new PopWindow(ThinkActivity.this, "取消收藏");
        }

        dialog = Utils.getLoading(this);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //解析数据库
                queryColl();
            }
        }.start();
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
                        Intent textIntent = new Intent(Intent.ACTION_SEND);
                        textIntent.setType("text/plain");
                        textIntent.putExtra(Intent.EXTRA_TEXT,"脑筋急转弯：\n"+ njDatas.get(count - 1).getProblem() + "\n你知道答案吗，更多脑筋急转弯尽在笑话急转弯");
                        startActivity(Intent.createChooser(textIntent, "分享"));
                        break;
                    case R.id.tv_more_coll:
                        if (type == 0) {
                            //收藏
                            ContentValues value = new ContentValues();
                            value.put("coll", 1);
                            int think = database.update("think", value, "id = ?", new String[]{njDatas.get(count - 1).getId() + ""});
                            if (think > 0) {
                                Toast.makeText(ThinkActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //取消收藏
                            ContentValues value = new ContentValues();
                            value.put("coll", 0);
                            int think = database.update("think", value, "id = ?", new String[]{njDatas.get(count - 1).getId() + ""});
                            njDatas.remove(count - 1);
                            if (njDatas.size() == 0) {
                                ll_contorl.setVisibility(View.GONE);
                                title.setText("脑筋急转弯");
                                more.setVisibility(View.GONE);
                                ask.setVisibility(View.GONE);
                                answer.setVisibility(View.GONE);
                                no_coll.setVisibility(View.VISIBLE);
                                return;
                            }
                            count--;
                            if (count > njDatas.size()) {
                                count = njDatas.size();
                                ask.setText(njDatas.get(count - 1).getProblem());
                                answer.setText("点击显示答案");
                            } else {
                                if (count <= 0) {
                                    count = njDatas.size();
                                    ask.setText(njDatas.get(count - 1).getProblem());
                                    answer.setText("点击显示答案");
                                } else {
                                    ask.setText(njDatas.get(count - 1).getProblem());
                                    answer.setText("点击显示答案");
                                }

                            }
                            title.setText(titleStr+"("+count+"/"+njDatas.size()+")");
                            if (think > 0) {
                                Toast.makeText(ThinkActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.tv_more_copy:
                        String currentContent3 = "脑筋急转弯：\n"+ njDatas.get(count - 1).getProblem();
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                        ClipData clipData = ClipData.newPlainText(null, currentContent3);
                        // 把数据集设置（复制）到剪贴板
                        cm.setPrimaryClip(clipData);
                        Toast.makeText(ThinkActivity.this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }



    public void queryColl() {
        Cursor cursor;
        if (type==0){
             cursor = database.rawQuery("select * from think where type = ?", new String[]{titleStr});
        }else{
             cursor = database.rawQuery("select * from think where coll = ?", new String[]{type + ""});
        }

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            NJ nj = new NJ(title, content, id);
            njDatas.add(nj);
        }
        //乱序处理
        if (type==0){
            Collections.shuffle(njDatas);
        }
        handler.sendEmptyMessage(0);
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.answer:
                answer.setText(njDatas.get(count - 1).getAnswer());
                break;
            case R.id.pre:
                if (count <= 1) {
                    Toast.makeText(ThinkActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
                } else {
                    count--;
                    title.setText(titleStr+"("+count+"/"+njDatas.size()+")");
                    ask.setText(njDatas.get(count - 1).getProblem());
                    answer.setText("点击显示答案");
                }
                break;
            case R.id.next:
                    if (count >= njDatas.size()) {
                        Toast.makeText(ThinkActivity.this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        count++;
                        title.setText(titleStr+"("+count+"/"+njDatas.size()+")");
                        ask.setText(njDatas.get(count - 1).getProblem());
                        answer.setText("点击显示答案");
                    }

                break;

        }
    }

}
