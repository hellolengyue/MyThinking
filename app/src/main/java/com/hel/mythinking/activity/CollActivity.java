package com.hel.mythinking.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.adapter.PicRecyclerViewAdapter;
import com.hel.mythinking.bean.PicBean;
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

/**
 * @author hel
 * @date 2018/6/11
 * 文件 MyThinking
 * 描述
 */

public class CollActivity extends BaseActivity {

    private XRecyclerView recyclerView;
    private List<PicBean.ResultsBean> datas = new ArrayList<>();
    private PicRecyclerViewAdapter adapter;
    private SQLiteDatabase database;
    private Context context;
    private LinearLayout no_coll;
    private Button back;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    no_coll.setVisibility(View.GONE);
                    break;
                case 1:
                    Toast.makeText(context, getResources().getString(R.string.save_success) + ALBUM_PATH, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context, R.string.save_fail, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    no_coll.setVisibility(View.VISIBLE);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll);
        back = (Button) findViewById(R.id.back);
        context = this;
        database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        initAdData();
        getDataAsync();
        no_coll = (LinearLayout) findViewById(R.id.no_coll);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(5, 5, 5, 5);
//                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        adapter = new PicRecyclerViewAdapter(context, datas);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PicRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //打开大图
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("type","coll");
//                intent.putExtra("url", datas.get(position - 1).getUrl());
//                intent.putExtra("type", "fragment");
                intent.putExtra("position", position - 1);
                intent.putExtra("datas", (Serializable) datas);
                startActivityForResult(intent,1);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //弹出菜单

                setDialog(position);
            }
        });
    }

    /**
     * 读取数据库
     */
    private void getDataAsync() {
        Cursor cursor = database.query( true,"pic", null,null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String url = cursor.getString(cursor.getColumnIndex("url"));
            PicBean.ResultsBean resultsBean = new PicBean.ResultsBean(url);
            datas.add(resultsBean);

        }
        if (datas.size() <= 0) {

            handler.sendEmptyMessage(3);
        } else {

            handler.sendEmptyMessage(0);
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==2&data!=null){
            int position = data.getIntExtra("position", -1);
            datas.remove(position - 1);
            if (datas.size() == 0) {
                no_coll.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();

        }
    }

    private void setDialog(final int position) {
        final Dialog mCameraDialog = new Dialog(context, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(
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
        Button btn_coll = root.findViewById(R.id.btn_coll);
        btn_coll.setText("取消收藏");
        btn_coll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消收藏
                int think = database.delete("pic", "url=?", new String[]{datas.get(position - 1).getUrl()});
                datas.remove(position - 1);
                if (datas.size() == 0) {
                    no_coll.setVisibility(View.VISIBLE);
                }
                if (think > 0) {
                    Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
//                    adapter.notifyItemRemoved(position - 1);
                    adapter.notifyDataSetChanged();
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
        context.sendBroadcast(scannerIntent);
        bas.flush();
        bas.close();
        //保存成功发送handler
        handler.sendEmptyMessage(1);
    }

}
