package com.appbyme.app272523.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.appbyme.app272523.base.util.ImageUtils;
import com.appbyme.app272523.base.util.InitUtils;
import com.appbyme.app272523.base.util.LoadImageUtils;
import com.kit.utils.AppUtils;
import com.kit.utils.ZogUtils;

import cn.jpush.android.api.JPushInterface;

public class ClanApplication extends Application {


    public static ClanApplication clanApplication;

    private static NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String processName = AppUtils.getProcessName(this, android.os.Process.myPid());

        if (processName != null) {
            boolean defaultProcess = processName.equals(AppUtils.getAppPackage(this));

            ZogUtils.printError(ClanApplication.class, "processName:" + processName
                    + " getAppPackage:" + AppUtils.getAppPackage(this) + " defaultProcess:" + defaultProcess);

            if (defaultProcess) {
                initAppForMainProcess();
            } else if (processName.contains(":remote")) {
                initAppForRemoteProcess();
            }
        }


    }

    public void initAppForRemoteProcess() {
    }

    public void initAppForMainProcess() {
        InitUtils.initEnvironment(this);


        CrashHandler.getInstance().init(this);

        InitUtils.initConfigFromSharedPerference(this);

//        EmoticonsUtils.initEmoticonsDB(this);

        ImageUtils.initImageLoader(this);

        LoadImageUtils.init(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        clanApplication = this;

    }


    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static ClanApplication getApplication() {
        return clanApplication;
    }
}
