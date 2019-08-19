package com.hel.mythinking.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hel.mythinking.utils.BarUtils;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hel
 * @date 2018/8/23
 * 文件 MyThinking
 * 描述
 */

public abstract class BaseActivity extends AppCompatActivity {
    public String Adwo_PID = "f152c0bdfd9d4d89ab3d8ad96b8c9ce8";
    private InterstitialAd ad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        initView();
    }

    protected abstract int getLayout();
    protected abstract void initView();

    public void initAdData() {
        //全屏广告实例
        ad = new InterstitialAd(this, Adwo_PID, false, new InterstitialAdListener() {
            @Override
            public void onReceiveAd() {
                Log.e("hel", "onReceiveAd: ");
            }

            @Override
            public void onLoadAdComplete() {
                // 成功完成下载后，展示广告
                ad.displayAd();


            }

            @Override
            public void onFailedToReceiveAd(ErrorCode errorCode) {
                Log.e("hel", "onFailedToReceiveAd: " + errorCode);
            }

            @Override
            public void onAdDismiss() {
                Log.e("hel", "onAdDismiss: ");
            }

            @Override
            public void OnShow() {
                Log.e("hel", "OnShow: ");
            }
        });
        // 设置全屏格式
        ad.setDesireAdForm(InterstitialAd.ADWO_INTERSTITIAL);
        // 设置请求广告类型 可选。
        ad.setDesireAdType((byte) 0);
        // 开始请求全屏广告
        ad.prepareAd();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
