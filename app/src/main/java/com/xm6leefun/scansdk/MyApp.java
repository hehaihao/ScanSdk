package com.xm6leefun.scansdk;

import android.app.Application;

import com.xm6leefun.scan_lib.net.ApiConfig;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/4 14:11
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiConfig.init(BuildConfig.DEBUG,this);
    }
}
