package com.hel.mythinking.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.PicRecyclerViewAdapter;
import com.hel.mythinking.bean.PicBean;
import com.hel.mythinking.utils.Utils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hel
 * @date 2018/2/27
 * 文件 MyThinking
 * 描述
 */

public class PicActivity extends BaseActivity {
    private XRecyclerView recyclerView;
    private List<PicBean.ResultsBean> datas = new ArrayList<>();
    private PicRecyclerViewAdapter adapter;
    private int page = 1;
    private SQLiteDatabase database;
    private Dialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                    recyclerView.loadMoreComplete();
                    break;
                case 1:
                    Toast.makeText(PicActivity.this, getResources().getString(R.string.save_success) + ALBUM_PATH, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(PicActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    dialog.dismiss();
                    Toast.makeText(PicActivity.this, R.string.load_fail, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected int getLayout() {
        return R.layout.fragment_four;
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        initAdData();
        getDataAsync(page);
        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(PicActivity.this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(5, 5, 5, 5);
//                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        adapter = new PicRecyclerViewAdapter(PicActivity.this, datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getDataAsync(1);
            }

            @Override
            public void onLoadMore() {
                getDataAsync(++page);
            }
        });
        adapter.setOnItemClickListener(new PicRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //打开大图
                Intent intent = new Intent(PicActivity.this, ViewImageActivity.class);
                intent.putExtra("type", "fragment");
                intent.putExtra("position", position - 1);
                intent.putExtra("datas", (Serializable) datas);
                intent.putExtra("page", page);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                setDialog(position);
            }
        });
    }


    private void setDialog(final int position) {
        final Dialog mCameraDialog = new Dialog(PicActivity.this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(PicActivity.this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraDialog.dismiss();
                //开子线程下载并保存图片
                new Thread() {
                    public void run() {
                        try {
                            //获取图片的路径
                            String path = datas.get(position - 1).getUrl();
                            //截取图片路径中的图片名称
                            String mfileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                            //创建bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(getImageStream(path));
                            //保存图片到本地
                            saveFile(bitmap, mfileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        }
                    }

                }.start();
            }
        });
        root.findViewById(R.id.btn_coll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏
                ContentValues values = new ContentValues();
                values.put("url", datas.get(position - 1).getUrl());
                long think = database.insert("pic", null, values);
                if (think > 0) {
                    Toast.makeText(PicActivity.this, R.string.coll_success, Toast.LENGTH_SHORT).show();
                }
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraDialog.dismiss();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    public InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * 图片的保存路径
     */
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory().getPath() + Constant.SAVEPATH;

    /**
     * 保存文件
     *
     * @param bm
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        //如果文件夹不存在,先创建图片文件件
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String URLPath = ALBUM_PATH + fileName;
        File myCaptureFile = new File(URLPath);
        //获取文件输出流
        FileOutputStream bas = new FileOutputStream(myCaptureFile);
        //图片压缩,100是不压缩,保存原图
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bas);

        Uri uri = Uri.fromFile(new File(dirFile, fileName));
        // 通知图库更新
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        PicActivity.this.sendBroadcast(scannerIntent);
        bas.flush();
        bas.close();
        //保存成功发送handler
        handler.sendEmptyMessage(1);
    }

    private void getDataAsync(int page) {
        dialog = Utils.getLoading(PicActivity.this);
        dialog.show();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://gank.io/api/data/福利/20/" + page)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    String json = response.body().string();
                    Gson gson = new Gson();
                    PicBean picBean = gson.fromJson(json, PicBean.class);
                    List<PicBean.ResultsBean> results = picBean.getResults();
                    datas.addAll(results);
//
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

}
