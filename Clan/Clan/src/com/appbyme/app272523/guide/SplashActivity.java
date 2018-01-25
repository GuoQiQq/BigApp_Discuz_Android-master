package com.appbyme.app272523.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
        // 如果不是第一次启动app，则正常显示启动屏
//        setContentView(R.layout.activity_guide);
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                enterHomeActivity();
//            }
//        },1000);
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        finish();
    }
}

