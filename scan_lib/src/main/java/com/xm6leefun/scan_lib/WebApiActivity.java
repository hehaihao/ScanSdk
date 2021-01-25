package com.xm6leefun.scan_lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xm6leefun.scan_lib.utils.CircularAnimUtil;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/1/22 16:42
 */
public class WebApiActivity extends Activity {
    private WebView web;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_api);
        web = findViewById(R.id.web);
        WebSettings webSetting = web.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSetting.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setBlockNetworkImage(false);
        webSetting.setUserAgentString(webSetting.getUserAgentString() + ";ScanSDK");
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //https忽略证书问题
                handler.proceed();
            }

        });
        web.addJavascriptInterface(new ScanSDKJsInterface(),"ScanSDKJs");
        web.loadUrl("file:///android_asset/index.html");
    }

    private static final int SCAN_CODE = 0x1024;
    private class ScanSDKJsInterface{
        /**
         * 打开扫一扫
         */
        @JavascriptInterface
        public void appCamera() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WebApiActivity.this, ScanApiActivity.class);
                    CircularAnimUtil.startActivityForResult(WebApiActivity.this, intent,SCAN_CODE, web,R.color.tran);
                }
            });
        }

        @JavascriptInterface
        public void showToast(final String message){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WebApiActivity.this,message,Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void back(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case SCAN_CODE:
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString(ScanApiActivity.SCAN_RESULT,"");
                    String mBarcodeFormat = bundle.getString(ScanApiActivity.RESULT_TYPE,"");
                    String call="";
                    if ("DATA_MATRIX".equals(mBarcodeFormat)) {  // DM码
                        call = "javascript:appTest(\"" + mBarcodeFormat + "," + scanResult + "\")";
                    } else {
                        call ="javascript:appTest(\"" + scanResult + "\")" ;
                    }
                    web.loadUrl(call);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(web != null && web.canGoBack()){
                web.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(web != null){
            web.stopLoading();
            web.onPause();
            web.clearCache(true);
            web.clearHistory();
            web.removeAllViews();
            web.destroyDrawingCache();
            ViewGroup parent = (ViewGroup) web.getParent();
            if(parent != null) parent.removeView(web);
            web.destroy();
        }
    }
}
