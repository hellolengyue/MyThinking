package com.hel.mythinking.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hel.mythinking.R;
import com.hel.mythinking.fragment.CollFragment;
import com.hel.mythinking.fragment.JokeFragment;
import com.hel.mythinking.fragment.ThinkFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends BaseActivity {
    private CollFragment oneFragment;
    private ThinkFragment thinkFragment;
    private JokeFragment threeFragment;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView title;
    private Button openMenu;
    private BottomNavigationViewEx bnve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        title = findViewById(R.id.title);
        openMenu = findViewById(R.id.open_menu);
        bnve = findViewById(R.id.bnve);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setItemIconTintList(null);
        bnve.setTextSize(11);
        bnve.setIconsMarginTop(20);
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if (thinkFragment == null) {
            thinkFragment = new ThinkFragment();
        }
        bnve.setCurrentItem(1);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, thinkFragment).commit();
        title.setText(R.string.title_think);
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_think:
                        if (thinkFragment == null) {
                            thinkFragment = new ThinkFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, thinkFragment).commit();
                        title.setText(R.string.title_think);
                        break;
                    case R.id.nav_joke:
                        if (threeFragment == null) {
                            threeFragment = new JokeFragment();

                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, threeFragment).commit();
                        title.setText(R.string.title_joke);
                        break;

                    case R.id.nav_coll:
                        if (oneFragment == null) {
                            oneFragment = new CollFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, oneFragment).commit();
                        title.setText(R.string.title_coll);
                        break;

                }
                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_new:
                        Intent intent = new Intent(MainActivity.this, NewDayActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_jingdian:
                        Intent intent1 = new Intent(MainActivity.this, JDActivity.class);
                        startActivity(intent1);
                        break;
//                    case R.id.nav_pic:
//                        Intent intent2 = new Intent(MainActivity.this, PicActivity.class);
//                        startActivity(intent2);
//                        break;
                    case R.id.nav_set:
                        Intent intent3 = new Intent(MainActivity.this, SetActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_good:
                        launchAppDetail(getPackageName(), null);
                        break;
                    case R.id.nav_exit:
                        finish();
                        break;
                }

                return false;
            }
        });
    }

    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            //market://details?id=   这个东东对应安卓所有的应用分发市场
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg))
                intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //判断用户是否点击了“返回键”
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //与上次点击返回键时刻作差
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                //大于2000ms则认为是误操作，使用Toast进行提示
//                Toast.makeText(this, R.string.click_exit, Toast.LENGTH_SHORT).show();
//                //并记录下本次点击“返回键”的时刻，以便下次进行判断
//                mExitTime = System.currentTimeMillis();
//            } else {
//                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
//                finish();
////                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onBackPressed() {
       drawerLayout.closeDrawers();
//        super.onBackPressed();
    }

}
