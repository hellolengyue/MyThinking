package com.hel.mythinking.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author hel
 * @date 2018/3/1
 * 文件 MyThinking
 * 描述
 */

public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            loading.hide();
            load.setVisibility(View.INVISIBLE);
            finish();
        }
    };

    private TextView load;
    private AVLoadingIndicatorView loading;
    private PathView pathView;
    private ImageView image;

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        loading = findViewById(R.id.AVLoadingIndicatorView);

        pathView = findViewById(R.id.pathView);
        image = findViewById(R.id.image_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setVisibility(View.GONE);
            pathView.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.VISIBLE);
            pathView.setVisibility(View.GONE);
        }
        loading.show();
        load = findViewById(R.id.load);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //申请权限
            EasyPermissions.requestPermissions(SplashActivity.this, "部分功能需要您授予权限，请选择允许", 0, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        } else {
            init();
        }
    }

    @AfterPermissionGranted(0)
    public void init() {
        if (pathView.getVisibility() == View.VISIBLE) {
            pathView.useNaturalColors();
            pathView.getPathAnimator()
                    .delay(100)
                    .duration(600)
                    .interpolator(new AccelerateDecelerateInterpolator())
                    .start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(image, "alpha", 0.5f, 1f);
            animator.setDuration(3000);//时间1s
            animator.start();
        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                //将数据复制到sd卡并解压
                String path = getCacheDir().getPath();
                if (!new File(path + "/data.zip").exists()) {

                    Utils.copyFilesFromRaw(SplashActivity.this, R.raw.data, "data.zip", path);
                }
                if (!new File(path + "/data").exists()) {

                    unzip(path + "/data.zip", path + "/data", "1022");
                }
                Constant.dataPath = path + "/data/";
                handler.sendEmptyMessageDelayed(0, 1800);


            }
        }.start();
    }
//
//    /**
//     * 获取SD卡缓存目录
//     *
//     * @param context 上下文
//     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
//     *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
//     *                {@link android.os.Environment#DIRECTORY_MUSIC},
//     *                {@link android.os.Environment#DIRECTORY_PODCASTS},
//     *                {@link android.os.Environment#DIRECTORY_RINGTONES},
//     *                {@link android.os.Environment#DIRECTORY_ALARMS},
//     *                {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
//     *                {@link android.os.Environment#DIRECTORY_PICTURES}, or
//     *                {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
//     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
//     */
//    public static File getExternalCacheDirectory(Context context, String type) {
//        File appCacheDir = null;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            if (TextUtils.isEmpty(type)) {
//                appCacheDir = context.getExternalCacheDir();
//            } else {
//                appCacheDir = context.getExternalFilesDir(type);
//            }
//
//            if (appCacheDir == null) {// 有些手机需要通过自定义目录
//                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/");
//            }
//
//        }
//        return appCacheDir;
//    }

    /**
     * 解压文件
     *
     * @param srcPath  压缩文件路径
     * @param destPath 解压路径
     * @param password 密码
     * @return
     */

    private boolean unzip(String srcPath, String destPath, String password) {
        File zipFile = new File(srcPath);
        try {
            ZipFile zFile = new ZipFile(zipFile);

            if (!zFile.isValidZipFile()) {//判断需要解压的源文件是否存在，合法，和是否损坏
                throw new ZipException("error");

            }

            if (zFile.isEncrypted()) {//判断是否有加密
                zFile.setPassword(password.toCharArray());
            }

            zFile.extractAll(destPath);//解压文件存放路径
            Log.e("hel", "unzip: 解压完成");
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setRationale("此功能需要存储权限，否则无法正常使用，是否打开设置")
                    .setPositiveButton("打开")
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .build()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
