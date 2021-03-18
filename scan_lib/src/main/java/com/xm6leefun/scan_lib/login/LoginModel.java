package com.xm6leefun.scan_lib.login;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.base.ResultBean;
import com.xm6leefun.scan_lib.login.bean.LoginResultBean;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.net.Request;
import com.xm6leefun.scan_lib.utils.encode.Base64Utils;
import com.xm6leefun.scan_lib.utils.encode.RSAUtils;

import java.security.PublicKey;
import java.util.LinkedHashMap;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/5 9:47
 */
public class LoginModel {
    private IView iView;
    private Activity activity;
    private Gson gson;

    public LoginModel(IView iView, Activity activity) {
        this.iView = iView;
        this.activity = activity;
        gson = new Gson();
    }

    /**
     * 登录
     * @param mobile
     * @param code
     */
    public void codeLogin(String mobile, String code) {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("code",code);
        params.put("mobile",mobile);
        iView.loading(activity.getString(R.string.loading_str));
        new Request(ApiConfig.API_login,params){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("LoginModel",json.toString());
                    final LoginResultBean resultBean = gson.fromJson(json.toString(),LoginResultBean.class);
                    final int code = resultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                iView.loginSuccess(resultBean.getRecord());
                            }else{
                                iView.onLoadFail(resultBean.getMsg());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e);
                }
            }
            @Override
            public void fail(final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iView.dismiss();
                        iView.onLoadFail(e.getMessage());
                    }
                });
            }
        };
    }

    /**
     * 登录
     * @param mobile
     * @param pwd
     */
    public void pwdLogin(String mobile, String pwd) {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("mobile",mobile);
        params.put("password",pwd);
        String json = gson.toJson(params);
        params.clear();
        PublicKey publicKey = null;
        try {
            publicKey = RSAUtils.loadPublicKey(RSAUtils.PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        byte[] bytes = RSAUtils.encryptData(json.getBytes(), publicKey);
        String encode = Base64Utils.encode(bytes);
        params.put("encrypt", encode);
        iView.loading(activity.getString(R.string.loading_str));
        new Request(ApiConfig.API_PSW_login,params){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("LoginModel",json.toString());
                    final LoginResultBean resultBean = gson.fromJson(json.toString(),LoginResultBean.class);
                    final int code = resultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                iView.loginSuccess(resultBean.getRecord());
                            }else{
                                iView.onLoadFail(resultBean.getMsg());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e);
                }
            }
            @Override
            public void fail(final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iView.dismiss();
                        iView.onLoadFail(e.getMessage());
                    }
                });
            }
        };
    }

    /**
     * 发送验证码
     * @param phone
     * @param type
     */
    public void sendCode(String phone, String type, final CodeLoginFragment.SendSmsCallBack callBack) {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("mobile",phone);
        params.put("type",type);
        new Request(ApiConfig.API_SenCode,params){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("AppraisalModel",json.toString());
                    final ResultBean resultBean = gson.fromJson(json.toString(), ResultBean.class);
                    final int code = resultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (200 == code) {
                                callBack.onSuccess();
                            }else{
                                iView.onLoadFail(resultBean.getMsg());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e);
                }
            }
            @Override
            public void fail(final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iView.onLoadFail(e.getMessage());
                    }
                });
            }
        };
    }

    public void clearUserData() {
        ApiConfig.getInstance().removeLoginData();
    }

    interface IView {
        void loading(String s);
        void dismiss();
        void loginSuccess(LoginResultBean.Entity entity);
        void onLoadFail(String errorMessage);
        void reLogin();
    }
}
