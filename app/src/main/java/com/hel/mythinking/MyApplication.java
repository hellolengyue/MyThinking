package com.hel.mythinking;

import android.app.Application;
import android.content.Context;

import com.hel.mythinking.utils.CrashHandler;
import com.hel.mythinking.utils.SharedPreferencesHelper;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;

/**
 * @author hel
 * @date 2018/2/23
 * 文件 GuessWord
 * 描述
 */

public class MyApplication extends Application {
    public static SharedPreferencesHelper sp;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //存储数据
        sp = new SharedPreferencesHelper(context, "my_word");
        /**
         * 异常上报初始化
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
         *  输出详细的Bugly SDK的Log；
         *  每一条Crash都会被立即上报；
         *  自定义日志将会在Logcat中输出。
         * 建议在测试阶段建议设置成true，发布时设置为false。
         */
        CrashReport.initCrashReport(getApplicationContext(), "b97d42fa0a", false);
        //应用升级初始化
        Bugly.init(getApplicationContext(), "b97d42fa0a", false);
        UMConfigure.init(context,"5b6be53fa40fa305aa00003a", "baidu", UMConfigure.DEVICE_TYPE_PHONE,null);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

    }

}
