package com.appbyme.app272523.app;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.appbyme.app272523.R;
import com.appbyme.app272523.base.BaseActivity;

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
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
//        Log.e("1756", UrlConstants.IP + UrlConstants.Getweizhang);
        webView.loadUrl("http://app.sqys.com/plugin.php?id=jameson_pdf:jameson_pdf");
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

            view.loadUrl(url);
            Log.e("QQQQQQQ","URL=======" + url);
            return true;
        }

    }
}
