package com.hel.mythinking.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.NewDayAdapter;
import com.hel.mythinking.bean.JokeData;
import com.hel.mythinking.utils.Utils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hel
 * @date 2018/8/24
 * 文件 MyThinking
 * 描述
 */

public class NewDayActivity extends BaseActivity {
    private XRecyclerView dayRecyclerView;
    private NewDayAdapter adapter;
    private List<JokeData.ResultBean> list = new ArrayList<>();
    private Dialog dialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            if (list.size() <= 0) {
                Toast.makeText(NewDayActivity.this,"没有更多推荐了",Toast.LENGTH_SHORT).show();
            } else {
                dayRecyclerView.loadMoreComplete();
                adapter.notifyDataSetChanged();
            }

        }
    };
    @Override
    protected int getLayout() {
        return R.layout.activity_new;
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog = Utils.getLoading(this);
        initData();
        dayRecyclerView = findViewById(R.id.day_recycler_view);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerView.setPullRefreshEnabled(false);
        dayRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initData();
            }
        });
        adapter = new NewDayAdapter(list, this);
        dayRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        dialog.show();
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://v.juhe.cn/joke/randJoke.php?key=a09ea6e958120265d3c2e90f82d5d469")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                JokeData jokeData = gson.fromJson(json, JokeData.class);
                List<JokeData.ResultBean> result = jokeData.getResult();
                list.clear();
                list.addAll(result);
                handler.sendEmptyMessage(0);
            }
        });
    }


}
