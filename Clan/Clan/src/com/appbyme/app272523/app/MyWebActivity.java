package com.appbyme.app272523.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class MyWebActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ll_back;
    private WebView webView;
//        private TextView download;
//    private LinearLayout downloadL;
//    private ProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_test_webview);
        initView();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
//        downloadL = (LinearLayout) findViewById(R.id.downloadL);

//        myProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        webView = (WebView) findViewById(R.id.webView1);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setUseWideViewPort(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
//        Log.e("1756", UrlConstants.IP + UrlConstants.Getweizhang);
        webView.loadUrl("http://app.sqys.com/plugin.php?id=jameson_pdf:jameson_pdf");
//        webView.loadUrl("http://app.sqys.com/plugin.php?id=hwh_member");
        webView.setWebViewClient(new HelloWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    myProgressBar.setVisibility(View.GONE);
////                    downloadL.setVisibility(View.VISIBLE);
//                } else {
//                    if (View.INVISIBLE == myProgressBar.getVisibility()) {
//                        myProgressBar.setVisibility(View.VISIBLE);
//                    }
//                    myProgressBar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
        });
//        download = (TextView) findViewById(R.id.download);
//        download.setOnClickListener(this);
//        ll_back = (ImageView) findViewById(R.id.ll_back);
//        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_back:
                finish();

                break;
        }
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            if (url == null) return false;
//            view.loadUrl(url);
//            Log.e("QQQQQQQ","URL=======" + url);
//            return true;
            try {
                //w微信支付
//                if (url.startsWith("http://app.sqys.com/plugin.php?")) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    startActivity(intent);
//                    return true;
//                }else
                {
                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Referer", "http://app.sqys.com");
                    view.loadUrl(url, extraHeaders);
                }
                return true;
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }
        }

    }
}
