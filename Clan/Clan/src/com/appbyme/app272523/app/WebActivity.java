package com.appbyme.app272523.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.appbyme.app272523.R;
import com.appbyme.app272523.app.config.AppConfig;
import com.appbyme.app272523.base.IDClan;
import com.appbyme.app272523.base.util.AppSPUtils;
import com.appbyme.app272523.base.util.CookieUtils;
import com.appbyme.app272523.base.util.theme.ThemeUtils;
import com.appbyme.app272523.login.LoginActivity;
import com.appbyme.app272523.thread.detail.MoreActionProviderCallback;
import com.kit.app.ActivityManager;
import com.kit.app.enums.AppStatus;
import com.kit.extend.ui.web.model.WebviewGoToTop;
import com.kit.extend.ui.web.webview.LoadMoreWebView;
import com.kit.utils.ActionBarUtils;
import com.kit.utils.ZogUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.youzu.android.framework.ViewUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.analysis.MobclickAgent;

public class WebActivity extends com.kit.extend.ui.web.WebActivity {
    private static final int PHOTO = 2;
    private ImageView ll_back;
    private WebView webView;
    //    private TextView download;
//    private LinearLayout downloadL;
    private ProgressBar myProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        initTheme();
        ActionBarUtils.setHomeActionBar(this, R.drawable.ic_back);

    }

    public void initTheme() {
        ThemeUtils.initTheme(this);
    }


    @Override
    public boolean getExtra() {
        super.getExtra();
        contentViewName = "activity_web";

        return true;
    }

    public boolean initWidget() {
        setOverflowShowingAlways();

        return super.initWidget();
    }

    public boolean loadData() {
        return super.loadData();
    }

    public boolean initWidgetWithData() {
        return super.initWidgetWithData();
    }


    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTitle(int resId) {
        getSupportActionBar().setTitle(resId);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ThemeUtils.initResource(this);

        switch (item.getItemId()) {
            case android.R.id.home:
                if (webFragment.getWebView().canGoBack()) {
                    if (webFragment.getWebTitle().equals("家医签约百城巡讲活动") || webFragment.getWebTitle().equals("修改资料") || webFragment.getWebTitle().equals("QQ登录") || webFragment.getWebTitle().equals("weixin") ||
                            webFragment.getWebTitle().equals("充值") || webFragment.getWebTitle().equals("基层医师")) {
                        this.finish();
                    } else {

                        // 返回键退回
                        webFragment.getWebView().goBack();
                    }
                } else {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @OnClick(R.id.back)
    public void back(View view) {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popActivity(this);
//        if(LoadingDialogFragment.getInstance(this)!=null){
//            LoadingDialogFragment.getInstance(this).dismissAllowingStateLoss();
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.isShowing = AppStatus.SHOWING;

        //统计时长
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        AppConfig.isShowing = AppStatus.BACKGROUND;

        MobclickAgent.onPause(this);
    }

    @Override
    public void initWebView() {
        super.initWebView();

        webFragment.setGoToTopPosition(getGoToTopPosition());

        webFragment.setWebScrollListener(new LoadMoreWebView.WebScrollListener() {

                                             @Override
                                             public void onScroll(int dx, int dy) {
                                             }

                                             @Override
                                             public void onTop() {
                                             }

                                             @Override
                                             public void onCenter() {
                                             }

                                             @Override
                                             public void onBottom() {
                                                 ZogUtils.printLog(WebActivity.class, "bottom load more");
                                                 if (!webFragment.isRefreshing())
                                                     getData(false);
                                             }

                                         }
        );


        myProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        WebViewClient webViewClient = new WebViewClient() {
            @org.jetbrains.annotations.Contract("_, null -> false")
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if (url == null) return false;

                try {
                    //w微信支付
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://app.sqys.com");
                        wv.loadUrl(url, extraHeaders);
                    }
                    return true;
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
            }

        };
        webFragment.getWebView().setWebViewClient(webViewClient);
        //h5调用native方法
        webFragment.getWebView().addJavascriptInterface(this, "nativeMethod");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开

    }

    @JavascriptInterface
    public void toActivity() {
        //此处应该定义常量对应，同时提供给w写者
        startActivity(new Intent(this, LoginActivity.class));
    }

    MoreActionProviderCallback callback;

    @JavascriptInterface
    public void toShare() {
        //此处应该定义常量对应，同时提供给w写者
        callback.doShare();
    }

    //h5更换头像
//    @JavascriptInterface
//    public void openPhoto() {
//
//        // 启动本地相册
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        intent.putExtra("crop", "true"); //使用系统自带的裁剪
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1); //两行表示裁剪宽高比为1:1
////        intent.putExtra("outputX", WIDTH);
////        intent.putExtra("outputY", HEIGTH); //裁剪以后输出的宽高值
//        intent.putExtra("return-data", true); // 返回得到的数据
//        startActivityForResult(intent,2);
////        onActivityResult(PHOTO, ImageLibRequestResultCode.REQUEST_SELECT_PIC,intent);
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data != null) {
//            Bitmap bitmap = null;
//            if (data.hasExtra("data"))   // 首先会判断data中是否存在"data"值
//            {
//                bitmap = data.getParcelableExtra("data");  // 存在就可以直接取了
//            } else // 不存在的时候则是可以通过BitmapFactory来获取bitmap的值
//            {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                InputStream is = null;
//                try {
//                    is = getContentResolver().openInputStream(data.getData());
//                    bitmap = BitmapFactory.decodeStream(is, null, options);
//                } catch (Exception e) {
//                } finally {
//                    try {
//                        is.close();
//                    } catch (Exception e) {
//                    }
//                }
//            }
//        }
//    }

    private void getData(final boolean isJumpPage) {

    }


    @Override
    public void setCookieFromCookieStore() {
        cookies = CookieUtils.getCookies(this);
        webFragment.setCookieFromCookieStore(this, content, cookies);
    }


    private int getGoToTopPosition() {
        if (AppSPUtils.getShowGoToTop(WebActivity.this) == IDClan.ID_RADIOBUTTON_SHOW_GO_TO_TOP_RIGHT) {
            return WebviewGoToTop.RIGHT;
        } else if (AppSPUtils.getShowGoToTop(WebActivity.this) == IDClan.ID_RADIOBUTTON_SHOW_GO_TO_TOP_LEFT) {
            return WebviewGoToTop.LEFT;
        } else
            return WebviewGoToTop.NONE;
    }
}