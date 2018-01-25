package com.appbyme.app272523.main.bottomtab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.appbyme.app272523.R;
import com.appbyme.app272523.app.config.BuildType;
import com.appbyme.app272523.app.constant.Key;
import com.appbyme.app272523.base.net.DoSignIn;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.ClanUtils;
import com.appbyme.app272523.base.util.ServiceUtils;
import com.appbyme.app272523.base.util.StringUtils;
import com.appbyme.app272523.base.util.ToastUtils;
import com.appbyme.app272523.base.util.jump.JumpWebUtils;
import com.appbyme.app272523.base.util.view.MainTopUtils;
import com.appbyme.app272523.base.util.view.ViewDisplayUtils;
import com.appbyme.app272523.common.WebFragment;
import com.appbyme.app272523.main.base.activity.BaseMainActivity;
import com.appbyme.app272523.main.wechatstyle.MineProfileFragment;
import com.appbyme.app272523.message.MessageFragment;
import com.appbyme.app272523.upload.ParseXmlService;
import com.kit.utils.DialogUtils;
import com.kit.utils.ListUtils;
import com.kit.utils.ZogUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.youzu.android.framework.view.annotation.ContentView;
import com.youzu.android.framework.view.annotation.ViewInject;
import com.youzu.android.framework.view.annotation.event.OnClick;
import com.youzu.clan.base.common.Action;
import com.youzu.clan.base.enums.BottomButtonType;
import com.youzu.clan.base.json.ProfileJson;
import com.youzu.clan.base.json.homepageconfig.ButtonConfig;
import com.youzu.clan.base.json.homepageconfig.HomepageVariables;
import com.youzu.clan.base.json.homepageconfig.ViewTabConfig;
import com.youzu.clan.base.json.profile.ProfileVariables;
import com.youzu.clan.base.json.profile.Space;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zhao on 15/6/24.
 */
@ContentView(R.layout.activity_main_bottom_tab)
public class BottomTabMainActivity extends BaseMainActivity {
    private static final String TAG = "SplashActivity";
    public static final int SHOW_ERROR = 1;

    public static int MESSAGE_POSITION = 3;
    public static int NOW_POSITION_IN_VIEWPAGER = 0;

    @ViewInject(R.id.top)
    private LinearLayout mTop;


    private static ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private long exitTime;

    private static com.appbyme.app272523.main.bottomtab.TabholderFragment tabholderFragment;
    private HomepageVariables homepageVariables;
    private ProfileVariables mProfileVariables;

//    private Skeleton skeleton;

    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Key.KEY_NEW_MESSAGE)) {

                int messageCount = sharedPreferences.getInt(key, 0);
                ZogUtils.printError(BottomTabMainActivity.class, "messageCount:" + messageCount);

                if (messageCount > 0) {
                    setRedDot(messageCount);

                    for (Fragment f : fragments) {
                        if (f instanceof MessageFragment) {
                            ((MessageFragment) f).refresh();
                        }
                    }
                } else {
                    tabholderFragment.getTabLayout().setNotifyText(MESSAGE_POSITION, "");
                }
            } else if (key.equals(Key.KEY_AVATAR)) {
                final String avatar = sharedPreferences.getString(Key.KEY_AVATAR, "");
//                displayMineAvatar(avatar);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                .penaltyLog().penaltyDeath().build());
        MainTopUtils.setActivityTopbar(this, mTop, R.layout.top_main, getString(R.string.forum_name), null);
//        ibSearch = (ImageButton) views.get(8);

        if (isUpdate()) {
            try {
                showDialogUpdate();
            } catch (Exception e) {

            }
        }
        fragments.clear();

        homepageVariables = AppSPUtils.getHomePageConfigJson(this).getVariables();
        ArrayList<ButtonConfig> buttonConfigs = homepageVariables.getButtonConfigs();

        for (int i = 0; i < buttonConfigs.size(); i++) {
            ButtonConfig bc = buttonConfigs.get(i);
            ZogUtils.printError(BottomTabMainActivity.class, bc.getButtonName() + " bc.getButtonType():" + bc.getButtonType());

            switch (bc.getButtonType()) {
                case BottomButtonType.HOME_PAGE:
                    ViewTabConfig viewTabConfigs = bc.getViewTabConfig();
                    fragments.add(ClanUtils.getChangeableHomeFragment(viewTabConfigs));
                    break;
                case BottomButtonType.FORUM_NAV:
                    fragments.add(ClanUtils.getForumNav(this));
                    break;
                case BottomButtonType.THREAD_PUBLISH:
//                    fragments.add(ClanUtils.getIndexFragment(this));
                    break;
                case BottomButtonType.MESSAGE:
                    fragments.add(new MessageFragment());
                    MESSAGE_POSITION = i;
                    break;
                case BottomButtonType.MINE:
                    MineProfileFragment mineProfileFragment = new MineProfileFragment();
                    fragments.add(mineProfileFragment);
//                    mineProfileFragment.setIbSignIn(MainTopUtils.ibSignIn);
                    break;
            }


        }

        ZogUtils.printError(BottomTabMainActivity.class, "fragments.size():" + fragments.size());

        tabholderFragment = com.appbyme.app272523.main.bottomtab.TabholderFragment.getInstance(MainTopUtils.tvPreDo, MainTopUtils.tvDo, fragments);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(com.kit.bottomtabui.R.id.main_container, tabholderFragment)
                    .commit();
        }


//        PollingUtils.startCheckNewMessage(this, mPreferenceListener);

        AppSPUtils.setSPListener(this, mPreferenceListener);


    }


    private void setRedDot(int count) {
        tabholderFragment.getTabLayout().setNotifyText(MESSAGE_POSITION, count + "");
        ViewDisplayUtils.setBadgeCount(this, count);

    }

    public void showDialog() {
        if (getString(R.string.build_type).equals(BuildType.TEST)) {
            DialogUtils.showDialog(BottomTabMainActivity.this, getString(R.string.tips), getString(R.string.notice_test_build), getString(R.string.confirm), getString(R.string.cancel), true, true, null, null);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        ZogUtils.printError(BottomTabMainActivity.class, "onRestart onRestart onRestart");


    }

    @Override
    protected void onResume() {
        super.onResume();
        ZogUtils.printError(BottomTabMainActivity.class, "onResume");
        ServiceUtils.startClanService(this, Action.ACTION_CHECK_NEW_MESSAGE);

        MainTopUtils.setActivityTopbar(this, mTop, R.layout.top_main, getString(R.string.forum_name), null);

        setTopbar(true, NOW_POSITION_IN_VIEWPAGER);

        homepageVariables = AppSPUtils.getHomePageConfigJson(this).getVariables();

    }

    @Override
    protected void onDestroy() {

        ZogUtils.printError(BottomTabMainActivity.class, "onDestroy onDestroy onDestroy onDestroy onDestroy onDestroy ");
        super.onDestroy();
        AppSPUtils.unsetSPListener(this, mPreferenceListener);
//        PollingUtils.stopCheckNewMessage(this, mPreferenceListener);
    }


    public static ArrayList<Fragment> getFragments() {
        return fragments;
    }

    /**
     * 更新界面
     *
     * @param t
     */
    private void updateUi(ProfileJson t) {
        if (t == null) {
            return;
        }
        ProfileVariables variables = t.getVariables();
        if (variables == null) {
            return;
        }
        mProfileVariables = variables;
        Space space = variables.getSpace();
        if (space == null) {
            return;
        }
        final String avatar = space.getAvatar();

        AppSPUtils.saveAvatarUrl(this, avatar);
        if (!StringUtils.isEmptyOrNullOrNullStr(variables.getMemberUsername()))
            AppSPUtils.saveUsername(this, variables.getMemberUsername());

    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        int positionInViewPager = tabholderFragment.getCurrInViewPager();
        if (positionInViewPager == 1) {
            MainTopUtils.tvTitle.setText("圈子");
        } else {

            if (!ListUtils.isNullOrContainEmpty(fragments)
                    && (fragments.get(positionInViewPager) instanceof WebFragment)) {
                MainTopUtils.tvTitle.setText(title);
            }
        }


    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        setTitle(title.toString());
    }


    /**
     * 设置主页顶部的按钮
     *
     * @param isUse               是否启用
     * @param positionInViewPager
     */
    public void setTopbar(boolean isUse, int positionInViewPager) {

        int position = tabholderFragment.getTabLayout().getRealPositionInBottomUILayout(positionInViewPager);

        NOW_POSITION_IN_VIEWPAGER = positionInViewPager;

//        ArrayList<ButtonConfig> buttonConfigs = homepageVariables.getButtonConfigs();
        ButtonConfig buttonConfig = homepageVariables.getButtonConfigs().get(position);
        if (positionInViewPager == 1) {
            MainTopUtils.tvTitle.setText("圈子");
        } else {

            MainTopUtils.setMainTopbar(BottomTabMainActivity.this, buttonConfig, fragments, positionInViewPager);
        }
    }


    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - exitTime > 2000) {
            ToastUtils.show(this, R.string.click_to_exit);
            exitTime = currentTime;
            return;
        }
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZogUtils.printError(BottomTabMainActivity.class, "onActivityResult onActivityResult onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);


    }


    /**
     * 提示版本更新的对话框
     */
    public void showDialogUpdate() throws Exception {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.drawable.ic_launcher).
                // 设置要显示的信息
                        setMessage("发现新版本！请及时更新").

                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this, "选择确定哦", 0).show();
//                        loadNewVersionProgress();//下载最新的版本程序
                        downloadApk();
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);

        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();

        DoSignIn.checkSignIn(this, null, DoSignIn.SIGN_IN, null);

    }


    /**
     * 从服务器端下载最新apk
     */
    private void downloadApk() {
        //显示下载进度  
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        //访问网络下载apk  
        new Thread(new DownloadApk(dialog)).start();
    }

    /**
     * 访问网络下载apk
     */
    private class DownloadApk implements Runnable {
        private ProgressDialog dialog;
        InputStream is;
        FileOutputStream fos;

        public DownloadApk(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = mHashMap.get("url");
            Request request = new Request.Builder().get().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "开始下载apk");
                    //获取内容总长度  
                    long contentLength = response.body().contentLength();
                    //设置最大值  
                    dialog.setMax((int) contentLength);
                    //保存到sd卡  
                    File apkFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".apk");
                    fos = new FileOutputStream(apkFile);
                    //获得输入流  
                    is = response.body().byteStream();
                    //定义缓冲区大小  
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度  
                            dialog.setProgress(progress);
                        } catch (InterruptedException e) {
                            Message msg = Message.obtain();
                            msg.what = SHOW_ERROR;
                            msg.obj = "ERROR:10002";
//                            handler.sendMessage(msg);
//                            load2Login();
                        }
                    }
                    //下载完成,提示用户安装  
                    installApk(apkFile);
                }
            } catch (IOException e) {
                Message msg = Message.obtain();
                msg.what = SHOW_ERROR;
                msg.obj = "ERROR:10003";
//                handler.sendMessage(msg);
//                load2Login();
            } finally {
                //关闭io流  
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is = null;
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
            dialog.dismiss();
        }
    }

    /**
     * 下载完成,提示用户安装
     */
    private void installApk(File file) {
        //调用系统安装程序  
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 2);
    }

    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private boolean isUpdate() {
        // 获取当前软件版本
        int versionCode = getVersion();
        getNewsInfo();
        // 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
//        Log.e("mHashMap",mHashMap.get("version"));
//        ToastUtils.show(this, "本地号1 " + versionCode +"  服务器版本号1 " + mHashMap.get("version"));
        if (null != mHashMap) {
            int serviceCode;
            String s = mHashMap.get("version");
            serviceCode = Integer.valueOf(s);
//            ToastUtils.show(this, "本地号 " + versionCode + "  服务器版本号 " + serviceCode);
//            Log.e("serviceCode", "serviceCode" + serviceCode);
            // 版本判断
            if (serviceCode > versionCode) {
                return true;
            }
        }

        return false;
    }

    private void getNewsInfo() {
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
        Log.v("tag", "1111111111");
        String path = "http://app.sqys.com/version.xml";
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
//                  conn.connect();
            //发送http GET请求，获取相应码
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                //使用pull解析器，解析这个流
                ParseXmlService service = new ParseXmlService();
                mHashMap = service.parseXml(is);
//                ToastUtils.show(this, "本地号 " + getVersion() + "  服务器版本号 " + mHashMap.get("version"));
//                Log.e("mHapMap11111111111", mHashMap.get("version"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//
//            }

//        };
//        thread.start();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setmHashMap(HashMap<String, String> mHashMap) {
        this.mHashMap = mHashMap;
    }

    public HashMap<String, String> getmHashMap() {
        return mHashMap;
    }

    /**
     * 首页悬浮按钮
     *
     * @param view
     */
    @OnClick(R.id.sign_btn)
    public void sign_btn(View view) {
        JumpWebUtils.toWeb1(this, "", "http://app.sqys.com/plugin.php?id=cack_app_litebl&cmod=code");
    }
}