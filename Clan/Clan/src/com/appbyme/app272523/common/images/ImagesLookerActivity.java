package com.appbyme.app272523.common.images;

import android.os.Bundle;
import android.view.Menu;

import com.kit.app.ActivityManager;
import com.appbyme.app272523.base.util.theme.ThemeUtils;

import cn.sharesdk.analysis.MobclickAgent;

/**
 * Created by Zhao on 15/10/16.
 */
public class ImagesLookerActivity extends com.kit.imagelib.imagelooker.ImagesLookerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        ActivityManager.getInstance().pushActivity(this);
    }

    public void initTheme() {
        ThemeUtils.initTheme(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
