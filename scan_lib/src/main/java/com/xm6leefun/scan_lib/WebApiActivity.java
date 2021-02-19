package com.xm6leefun.scan_lib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.huawei.hms.ml.scan.HmsScan;


/**
 * @Description:溯源H5
 * @Author: hhh
 * @CreateDate: 2021/1/22 16:42
 */
public class WebApiActivity extends Activity {

    public static void jump(Context context,String url){
        Intent intent = new Intent(context,WebApiActivity.class);
        Bundle args = new Bundle();
        args.putString(URL,url);
        intent.putExtras(args);
        context.startActivity(intent);
    }

    private static final String URL = "url";
    private String url = "";
    //权限数组（申请定位）
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    //返回code
    private static final int OPEN_SET_REQUEST_CODE = 100;
    private WebView web;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_api);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,OPEN_SET_REQUEST_CODE);
        }
        web = findViewById(R.id.web);
        WebSettings webSetting = web.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSetting.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setBlockNetworkImage(false);
        webSetting.setAppCacheEnabled(false);

        webSetting.setUserAgentString(webSetting.getUserAgentString() + ";TrueValue");
        web.setWebChromeClient(webChromeClient);
        web.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("zwd") && url.contains(".apk")){//过滤真唯度下载链接，因为已经在真唯度里打开H5了（或者让H5去限制是否可点击真唯度图标）
                    return true;
                }
                if (url.contains(".apk") || url.endsWith(".apk")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //https忽略证书问题
                handler.proceed();
            }

        });
        web.addJavascriptInterface(new ScanSDKJsInterface(),"js");
        Bundle args = getIntent().getExtras();
        if(args != null) url = args.getString(URL,"");
        web.loadUrl(url);
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    };

    private class ScanSDKJsInterface{
        /**
         * 打开扫一扫
         */
        @JavascriptInterface
        public void appCamera() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HwScanApiActivity.jumpForResult(WebApiActivity.this,web);
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

        @JavascriptInterface
        public void openBlockBrowser(final String url){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WebApiActivity.jump(WebApiActivity.this, url);
                }
            });
        }
        //跳转个人中心
        @JavascriptInterface
        public void jumpPersonal(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toPersonalPage();
                }
            });
        }
        //SDK中登录处理跳转个人中心
        @JavascriptInterface
        public void login(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toPersonalPage();
                }
            });
        }

        /**
         * 跳转h5个人中心
         */
        private void toPersonalPage(){
            if(url.contains("#")){
                String personalUrl = url.split("#")[0] + "#/personal";
                web.loadUrl(personalUrl);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            String call="";
            switch (requestCode){
                case ScanApiActivity.SCAN_CODE:
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString(ScanApiActivity.SCAN_RESULT,"");
                    String mBarcodeFormat = bundle.getString(ScanApiActivity.RESULT_TYPE,"");
                    if ("DATA_MATRIX".equals(mBarcodeFormat)) {  // DM码
                        call = "javascript:appTest(\"" + mBarcodeFormat + "," + scanResult + "\")";
                    } else {
                        call ="javascript:appTest(\"" + scanResult + "\")" ;
                    }
                    web.loadUrl(call);
                    break;
                case HwScanApiActivity.SCAN_CODE:
                    HmsScan result = data.getParcelableExtra(HwScanApiActivity.SCAN_RESULT);
                    if(result != null){
                        String code = result.getOriginalValue();
                        if (result.getScanType() == HmsScan.DATAMATRIX_SCAN_TYPE){
                            call = "javascript:appTest(\"DATA_MATRIX," + code + "\")";
                        }else{
                            call ="javascript:appTest(\"" + code + "\")" ;
                        }
                        web.loadUrl(call);
                    }
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

    /**
     * 关闭当前界面
     * @param view
     */
    public void close(View view) {
        finish();
    }
}
