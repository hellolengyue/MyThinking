package com.hel.mythinking.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hel.mythinking.R;

/**
 * @author hel
 * @date 2018/8/9
 * 文件 MyThinking
 * 描述
 */

public class PopWindow extends PopupWindow {
    private View conentView;
    private ItemClick itemClick;

    public PopWindow(final Activity context, String coll) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.more_item, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w * 2 / 5);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        TextView textView = conentView.findViewById(R.id.tv_coll);
        textView.setText(coll);
        conentView.findViewById(R.id.tv_more_share).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //do something you need here
                itemClick.itemClick(R.id.tv_more_share);
                PopWindow.this.dismiss();
            }
        });
        conentView.findViewById(R.id.tv_more_coll).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // do something before signing out
                itemClick.itemClick(R.id.tv_more_coll);
                PopWindow.this.dismiss();
            }
        });
        conentView.findViewById(R.id.tv_more_copy).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // do something before signing out
                itemClick.itemClick(R.id.tv_more_copy);
                PopWindow.this.dismiss();
            }
        });


    }

    public void setOnItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, parent.getLayoutParams().width / 2);
        } else {
            this.dismiss();
        }
    }

    public interface ItemClick {
        void itemClick(int res);
    }
}
