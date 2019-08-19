package com.hel.mythinking.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.PicViewPagerAdapter;
import com.hel.mythinking.bean.PicBean;
import com.hel.mythinking.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hel
 * @date 2017/12/15
 * 文件 KuaiLiao
 * 描述 浏览图片viewpager
 */


public class ViewImageActivity extends BaseActivity {
    private Context context;
    private SQLiteDatabase database;
    private Dialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    viewPagerAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    Toast.makeText(context, "保存成功" + ALBUM_PATH, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    dialog.dismiss();
                    Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    private String type;
    private int position;
    private List<PicBean.ResultsBean> datas;
    private ViewPager viewPager;
    private PicViewPagerAdapter viewPagerAdapter;
    private int page;

    @Override
    protected int getLayout() {
        return R.layout.activity_view_image;
    }

    @Override
    protected void initView() {
        context = this;
        database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        viewPager = findViewById(R.id.view_pager);
        Intent intent = getIntent();
//        url = intent.getStringExtra("url");
        type = intent.getStringExtra("type");
        position = intent.getIntExtra("position", -1);
        page = intent.getIntExtra("page", -1);
        datas = (List<PicBean.ResultsBean>) intent.getSerializableExtra("datas");
        if (datas != null) {
            viewPagerAdapter = new PicViewPagerAdapter(this, datas);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(position);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == datas.size() - 1 && type.equals("fragment")) {
                    //加载图片
                    getDataAsync(++page);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPagerAdapter.setOnItemListener(new PicViewPagerAdapter.setOnItemListener() {
            @Override
            public void onClickListener(int position) {
                finish();
            }

            @Override
            public void onLongClickListener(int position) {
                //弹出菜单
                setDialog(datas.get(position).getUrl());
            }
        });

    }

    private void getDataAsync(int page) {
        dialog = Utils.getLoading(context);
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

    private void setDialog(final String url) {
        final Dialog mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
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
                            //截取图片路径中的图片名称
                            String mfileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                            //创建bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(getImageStream(url));
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
        Button btn_coll = root.findViewById(R.id.btn_coll);
        if (type.equals("coll")) {
            btn_coll.setText("取消收藏");
        } else {
            btn_coll.setText("收藏");

        }
        btn_coll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraDialog.dismiss();
                if (type.equals("coll")) {
                    //取消收藏
                    int think = database.delete("pic", "url=?", new String[]{datas.get(position - 1).getUrl()});
                    if (think > 0) {
                        Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("position", position);
                        setResult(2, intent);
                        finish();
                    }
                } else {
                    //收藏
                    ContentValues values = new ContentValues();
                    values.put("url", url);
                    long think = database.insert("pic", null, values);
                    if (think > 0) {
                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                    }
                }


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
        this.sendBroadcast(scannerIntent);
        bas.flush();
        bas.close();
        //保存成功发送handler
        handler.sendEmptyMessage(1);
    }


}
