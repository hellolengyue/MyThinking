package com.hel.mythinking.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hel.mythinking.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * @author hel
 * @date 2018/8/6
 * 文件 MyThinking
 * 描述
 */

public class UIHelper {
    /**
     * 显示加载对话框
     *
     * @param context    上下文

     */
    public static void get(Context context,boolean show) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        TextView loadingText =  view.findViewById(R.id.id_tv_loading_dialog_text);
        AVLoadingIndicatorView avLoadingIndicatorView =  view.findViewById(R.id.AVLoadingIndicatorView);
        loadingText.setText("正在加载...");
        final Dialog mLoadingDialog = new Dialog(context, R.style.loading_dialog_style);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        mLoadingDialog.show();
        avLoadingIndicatorView.smoothToShow();
        mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mLoadingDialog.hide();
                    return true;
                }
                return false;
            }
        });

    }

}
