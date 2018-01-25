package com.appbyme.app272523.guide;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbyme.app272523.R;
import com.appbyme.app272523.app.ClanApplication;
import com.appbyme.app272523.app.config.AppConfig;
import com.appbyme.app272523.app.constant.Key;
import com.appbyme.app272523.base.BaseActivity;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.ClanUtils;
import com.appbyme.app272523.base.util.InitUtils;
import com.appbyme.app272523.main.base.forumnav.DBForumNavUtils;
import com.kit.app.core.task.DoSomeThing;
import com.kit.utils.ListUtils;
import com.kit.utils.MessageUtils;
import com.kit.utils.ZogUtils;
import com.kit.widget.numberprogressbar.NumberProgressBar;
import com.youzu.android.framework.JsonUtils;
import com.youzu.android.framework.view.annotation.ContentView;
import com.youzu.android.framework.view.annotation.ViewInject;
import com.youzu.clan.base.callback.JSONCallback;
import com.youzu.clan.base.json.ProfileJson;
import com.youzu.clan.base.json.config.content.ContentConfig;
import com.youzu.clan.base.json.forumnav.NavForum;
import com.youzu.clan.base.json.homepageconfig.HomePageJson;
import com.youzu.clan.base.json.profile.ProfileVariables;
import com.youzu.clan.base.net.ClanHttp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

    private static final int INIT_CONFIG_END = 2;
    private static final int INIT_CONTENT_CONFIG_END = 3;
    private static final int INIT_PROFILE_END = 6;
    private static final int INIT_HOME_PAGE_CONFIG_END = 7;
    private static final int INIT_FORUM_DATA_END = 8;
    private static final int INIT_CONFIG_ERROR = 101;
    private boolean isInitError = false;
    private String errorMsg = "";


    private ProfileVariables mProfileVariables;
    private ClanApplication mApplication;

    private long showTime;


    private int initSplashFlag = 0;

    private int initData = 0;


    @ViewInject(R.id.image)
    private ImageView mImageView;
    @ViewInject(R.id.progressBar)
    private NumberProgressBar progressBar;
    @ViewInject(R.id.errorMsg)
    private TextView errorView;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case INIT_CONFIG_END:
                    initSplashFlag++;
                    if (initSplashFlag >= 1) {
                        initContentConfig();
                        initProfile();
                        initHomePageConfig();
                        initForumData();
                    }
                    break;

                case INIT_HOME_PAGE_CONFIG_END:
                    HomePageJson homePageJson = AppSPUtils.getHomePageConfigJson(GuideActivity.this);
                    if (homePageJson == null || homePageJson.getVariables() == null || homePageJson.getVariables().getButtonConfigs() == null) {
                        ZogUtils.printError(GuideActivity.class, "homePageJson is errer   homePageJson is errer");
                        errorMsg += getString(R.string.homePage_config_error);
                        MessageUtils.sendMessage(mHander, INIT_CONFIG_ERROR);
                        return;
                    }
                    initData++;
                    ZogUtils.printError(GuideActivity.class, "INIT_HOME_PAGE_CONFIG_END initData:" + initData);

                    break;
                case INIT_CONTENT_CONFIG_END:
                    ContentConfig contentConfig = AppSPUtils.getContentConfig(GuideActivity.this);
                    if (contentConfig == null) {
                        errorMsg += getString(R.string.content_config_error);
                        MessageUtils.sendMessage(mHander, INIT_CONFIG_ERROR);
                        return;
                    }
                    initData++;
                    ZogUtils.printError(GuideActivity.class, "INIT_CONTENT_CONFIG_END initData:" + initData);

                    break;
                case INIT_PROFILE_END:
                    initData++;
                    ZogUtils.printError(GuideActivity.class, "INIT_PROFILE_END initData:" + initData);
                    break;
                case INIT_FORUM_DATA_END:
                    List<NavForum> forums = DBForumNavUtils.getAllNavForum(GuideActivity.this);
                    if (ListUtils.isNullOrContainEmpty(forums)) {
                        errorMsg += getString(R.string.forum_data_error);
                        ZogUtils.printError(GuideActivity.class, "forums data is errer   forums data is errer");
                        MessageUtils.sendMessage(mHander, INIT_CONFIG_ERROR);
                        return;
                    }
                    initData++;
                    ZogUtils.printError(GuideActivity.class, "INIT_FORUM_DATA_END initData:" + initData);
                    break;
                case INIT_CONFIG_ERROR:
                    isInitError = true;
                    progressBar.setReachedBarColor(getResources().getColor(R.color.blue));
                    progressBar.setProgressTextColor(getResources().getColor(R.color.blue));
                    errorView.setText(errorMsg);
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private Runnable mToMainRunnable = new Runnable() {

        @Override
        public void run() {
            toMain();

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (ClanApplication) getApplication();

        ClanUtils.loadMyFav(this);


        InitUtils.initShareSDK(getApplicationContext());

        //初始化广告配置
        InitUtils.initConfig(GuideActivity.this, new DoSomeThing() {
            @Override
            public void execute(Object... objects) {
                ZogUtils.printLog(GuideActivity.class, "initConfig initConfig initConfig");
                MessageUtils.sendMessage(mHander, INIT_CONFIG_END);
            }
        });

        mImageView.setImageResource(R.drawable.new_splash);

        progressBar();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHander.removeCallbacks(mToMainRunnable);
    }


    public void toSlashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 加载用户信息
     */
    private void initProfile() {
        ClanHttp.getProfile(this, new JSONCallback() {
                    public void onSuccess(Context ctx, String str) {
                        super.onSuccess(ctx, str);
                        try {
                            ProfileJson t = JsonUtils.parseObject(str, ProfileJson.class);
                            if (t == null || t.getVariables() == null) {
                                MessageUtils.sendMessage(mHander, INIT_PROFILE_END);
                                return;
                            }
                            mProfileVariables = t.getVariables();
                            ZogUtils.printError(GuideActivity.class, "mProfileVariables:" + mProfileVariables.getMemberUid());
                        } catch (Exception e) {

                        }
                        MessageUtils.sendMessage(mHander, INIT_PROFILE_END);

                    }

                    @Override
                    public void onFailed(Context cxt, int errorCode, String errorMsg) {
                        MessageUtils.sendMessage(mHander, INIT_PROFILE_END);
                    }
                }
        );
    }

    private void initForumData() {
        InitUtils.preLoadForumData(GuideActivity.this, new DoSomeThing() {
            @Override
            public void execute(Object... object) {
                ZogUtils.printLog(GuideActivity.class, "initForumData initForumData initForumData");
                MessageUtils.sendMessage(mHander, INIT_FORUM_DATA_END);
            }
        });
    }

    private void initHomePageConfig() {
        //初始化首页界面配置
        InitUtils.initHomePageConfig(GuideActivity.this, new DoSomeThing() {
            @Override
            public void execute(Object... objects) {
                ZogUtils.printLog(GuideActivity.class, "initHomePageConfig initHomePageConfig initHomePageConfig");
                MessageUtils.sendMessage(mHander, INIT_HOME_PAGE_CONFIG_END);
            }
        });
    }

    private void initContentConfig() {
        //初始化内容显示配置项
        InitUtils.initContentConfig(GuideActivity.this, new DoSomeThing() {
            @Override
            public void execute(Object... objects) {
                ZogUtils.printLog(GuideActivity.class, "initContentConfig initContentConfig initContentConfig");
                MessageUtils.sendMessage(mHander, INIT_CONTENT_CONFIG_END);
            }
        });
    }

    private void progressBar() {
        progressBar.setReachedBarColor(getResources().getColor(R.color.blue));
        progressBar.setProgressTextColor(getResources().getColor(R.color.blue));
        progressBar.setProgressTextSize(20);
        new Thread(new Runnable() {
            @Override
            public void run() {

                long time = 0;
                while (!isInitError) {
                    int i = progressBar.getProgress();
                    int max = progressBar.getMax();

                    if (i == 99 && initData < 4) {
                        //进度快走完了，但是数据结构还没有都完成
                        //不进行进度增加
                        ZogUtils.printError(GuideActivity.class, "time=" + time);
                    } else {
                        i++;
                    }
                    int v;
                    if (i / 25 > initData) {
                        if (i == 99) {
                            v = 5;
                        } else if (i > 75) {
                            v = 20;
                        } else if (i > 50) {
                            v = 15;
                        } else {
                            v = 10;
                        }
                    } else {
                        if (initData >= 4) {
                            i++;
                            v = 1;
                        } else {
                            v = 5;
                        }
                    }

                    progressBar.setProgress(i);
                    if (i >= max) {
                        long delayTime = showTime * 1000;
                        if (time > delayTime) {
                            delayTime = 0;
                        } else {
                            delayTime -= time;
                        }

                        mHander.postDelayed(mToMainRunnable, delayTime);
                        break;
                    }

                    try {
                        time += 50 * v;
                        Thread.sleep(50 * v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void showFailedSpalsh() {
        mImageView.setImageResource(R.drawable.new_splash);

    }

    public void toMain() {

        if (mProfileVariables == null || mProfileVariables.getMemberUid().equals("0")) {
            AppSPUtils.setLoginInfo(this, false, "0", "");
        }


        ZogUtils.printLog(GuideActivity.class, "toMain toMain toMain");
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            toSlashActivity();
        } else {
            toMainActivity(ClanUtils.getMain(this));
        }
    }
//    public void totomain(){
//        toMainActivity(ClanUtils.getMain(this));
//    }

    public void toMainActivity(Class clazz) {

        AppConfig.isNewLaunch = true;
        Intent intent = new Intent(GuideActivity.this, clazz);
        intent.putExtra(Key.KEY_PROFILE, mProfileVariables);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

        toMain();
        finish();
    }

    /*
 * 获取当前程序的版本名
 */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionName;
    }

    /*
 * 获取当前程序的版本号
 */
    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionCode;
    }


    /**
     * 提示版本更新的对话框
     */
    public void showDialogUpdate() {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.mipmap.ic_launcher).
                // 设置要显示的信息
                        setMessage("发现新版本！请及时更新").
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this, "选择确定哦", 0).show();
                        loadNewVersionProgress();//下载最新的版本程序
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);

        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();


    }

    /**
     * 下载新版本程序，需要子线程
     */
    private void loadNewVersionProgress() {
        final String uri = "http://www.apk.anzhi.com/data3/apk/201703/14/4636d7fce23c9460587d602b9dc20714_88002100.apk";
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        //启动子线程下载任务
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(uri, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 从服务器获取apk文件的代码
     * 传入网址uri，进度条对象即可获得一个File文件
     * （要在子线程中执行哦）
     */
    public static File getFileFromServer(String uri, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time = System.currentTimeMillis();//当前时间的毫秒数
            File file = new File(Environment.getExternalStorageDirectory(), time + "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}