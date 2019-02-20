package com.hel.mythinking.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.InterstitialAd;
import com.sixth.adwoad.InterstitialAdListener;

/**
 * @author hel
 * @date 2018/8/23
 * 文件 MyThinking
 * 描述
 */

public class BaseFragment extends Fragment {
    String Adwo_PID = "f152c0bdfd9d4d89ab3d8ad96b8c9ce8";
    private InterstitialAd ad;
    public void initAdData() {
        //全屏广告实例
        ad = new InterstitialAd(getActivity(), Adwo_PID, false, new InterstitialAdListener() {
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
}
