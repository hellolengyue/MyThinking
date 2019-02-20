package com.hel.mythinking.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author hel
 * @date 2018/8/27
 * 文件 MyThinking
 * 描述
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;
    private final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private final String FILE_NAME = "crash";
    private final String FILE_NAME_SUFFIX = ".trace";
    private static CrashHandler mInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
    private File file;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        Log.e(TAG, "init: " + "初始化CrashHandler");

    }

    /**
     * 当程序发生未捕获的异常时，执行这里
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "uncaughtException: " + "执行崩溃日志");
        try {
            dumpExceptionToSDCard(e);
//            uploadExceptionToService(e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 将异常信息以及手机软件等相关信息保存到本地
     * @param e
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable e) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            e.printStackTrace(pw);
            Log.e(TAG, "写入文件成功: " + file.getPath());
            pw.close();
        } catch (Exception e1) {
            Log.e(TAG, "dump crash info failed" + e1);
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version:");
        pw.print(pi.versionName);
        pw.print('_');
        pw.print(pi.versionCode);
        //Android版本号
        pw.print("OS Verson:");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.print(Build.VERSION.SDK_INT);
        //手机制造商
        pw.print("Vendor:");
        pw.print(Build.MANUFACTURER);
        //手机型号
        pw.print("Model:");
        pw.print(Build.MODEL);
        //CPU架构
        pw.print("CPU ABI:");
        pw.print(Build.CPU_ABI);


    }

    /**
     * 将异常信息发送到服务器
     * @param e
     * @throws IOException
     */
    private void uploadExceptionToService(Throwable e) throws IOException {
        Log.e(TAG, "开始上传文件: "+file.length()+"");
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://...")
                .post(RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), file))
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "上传失败"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, response.body().string());
            }
        });
    }

}
