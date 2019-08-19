package com.hel.mythinking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.mythinking.R;
import com.hel.mythinking.utils.Utils;

/**
 * @author hel
 * @date 2018/8/24
 * 文件 MyThinking
 * 描述
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private Button back;
    private LinearLayout setAbort;
    private LinearLayout setClear;
    private LinearLayout setUpdate;
    private LinearLayout setFeed;
    private Dialog dialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null) {
                dialog.dismiss();
                if (msg.what == 0) {

                    Toast.makeText(SetActivity.this, "清理完成", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(SetActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_set;
    }

    @Override
    protected void initView() {
        back = (Button) findViewById(R.id.back);
        setAbort = (LinearLayout) findViewById(R.id.set_abort);
        setClear = (LinearLayout) findViewById(R.id.set_clear);
        setUpdate = (LinearLayout) findViewById(R.id.set_update);
        setFeed = (LinearLayout) findViewById(R.id.set_feed);

        back.setOnClickListener(this);
        setAbort.setOnClickListener(this);
        setClear.setOnClickListener(this);
        setUpdate.setOnClickListener(this);
        setFeed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.set_abort:
                View view = View.inflate(SetActivity.this, R.layout.dialog_abort, null);
                TextView tvAbort = view.findViewById(R.id.tv_abort);
                String appName = getResources().getString(R.string.app_name);
                String appVersionName = getAppVersionName(SetActivity.this);
                tvAbort.setText("软件名称：" + appName + "\n" + "版本号：" + appVersionName + "\n开发者：开心的虾虾\n联系作者：864366322@qq.com");
                new AlertDialog.Builder(SetActivity.this).setTitle("关于软件")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("确定", null)
                        .show();
                break;
            case R.id.set_clear:
                dialog = Utils.getLoading(SetActivity.this);
                dialog.show();
                handler.sendEmptyMessageDelayed(0, 1000);
                break;
            case R.id.set_update:
                dialog = Utils.getLoading(SetActivity.this);
                dialog.show();
                handler.sendEmptyMessageDelayed(1, 1000);
                break;
            case R.id.set_feed:
                View view2 = View.inflate(SetActivity.this, R.layout.dialog, null);
                final EditText input2 = view2.findViewById(R.id.add_note_msg);
                new AlertDialog.Builder(SetActivity.this).setTitle("意见反馈")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(view2)
                        .setCancelable(false)
                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String inputString = input2.getText().toString();
                                if (inputString.equals("")) {
                                    Toast.makeText(getApplicationContext(), "内容为空！", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "感谢您的宝贵意见！", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

        }
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


}
