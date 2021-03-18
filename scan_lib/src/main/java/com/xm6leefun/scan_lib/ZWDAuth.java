package com.xm6leefun.scan_lib;

import com.xm6leefun.scan_lib.listener.AuthListener;

/**
 * @Description: 授权登录管理
 * @Author: hhh
 * @CreateDate: 2021/3/5 17:33
 */
public class ZWDAuth {
    private AuthListener mAuthListener;
    private static ZWDAuth instance;
    private ZWDAuth (){}

    public static synchronized ZWDAuth getInstance() {
        if (instance == null) {
            instance = new ZWDAuth();
        }
        return instance;
    }

    public void setZWDAuthListener(AuthListener listener){
        mAuthListener = listener;
    }

    public void onData(String data){
        if(mAuthListener != null)
            mAuthListener.onData(data);
    }
}
