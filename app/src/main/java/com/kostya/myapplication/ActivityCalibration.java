package com.kostya.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.InetAddress;

public class ActivityCalibration extends AppCompatActivity {
    private WebView mWebView;
    private InetAddress hostAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        mWebView = (WebView) findViewById(R.id.webView);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        boolean b = mWebView.getSettings().getAllowContentAccess();
        b = mWebView.getSettings().getAllowFileAccessFromFileURLs();
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        b = mWebView.getSettings().getAllowUniversalAccessFromFileURLs();
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        // указываем страницу загрузки

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //WebScalesClient.Cmd.WT.getParam();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void onEvent(InetAddress event) {
        hostAddress = event;
        EventBus.getDefault().removeStickyEvent(InetAddress.class);
        mWebView.loadUrl("http://"+ hostAddress.getHostAddress() +"/calibr.html");
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            //super.onReceivedHttpAuthRequest(view, handler, host, realm);
            handler.proceed("admin", "1234");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
