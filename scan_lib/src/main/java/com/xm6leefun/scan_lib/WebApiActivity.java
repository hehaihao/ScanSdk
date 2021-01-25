package com.xm6leefun.scan_lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setBlockNetworkImage(false);
        webSetting.setUserAgentString(webSetting.getUserAgentString() + ";ScanSDK");
        web.addJavascriptInterface(new ScanSDKJsInterface(),"ScanSDKJs");
        web.loadUrl("file:///android_asset/index.html");
    }

    private static final int SCAN_CODE = 0x1024;
    private String scanCallBack;
    private class ScanSDKJsInterface{
        /**
         * 打开扫一扫
         * @param scanCallBack 回调
         */
        @JavascriptInterface
        public void openScan(String scanCallBack) {
            WebApiActivity.this.scanCallBack = scanCallBack;
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
                    web.loadUrl("javascript:"+scanCallBack+"(\"" + scanResult + "\")");
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
