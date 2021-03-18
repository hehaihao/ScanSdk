package com.xm6leefun.scan_lib.appraisa;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xm6leefun.scan_lib.base.ResultBean;
import com.xm6leefun.scan_lib.appraisa.bean.ChipDrawResultBean;
import com.xm6leefun.scan_lib.dialog.LoginDialog;
import com.xm6leefun.scan_lib.exception.BaseException;
import com.xm6leefun.scan_lib.login.bean.LoginResultBean;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.net.Request;
import com.xm6leefun.scan_lib.utils.DealUrlDataUtils;

import java.util.LinkedHashMap;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/5 9:47
 */
public class AppraisalModel {
    private IView iView;
    private Activity activity;
    private Gson gson;

    public AppraisalModel(IView iView, Activity activity) {
        this.iView = iView;
        this.activity = activity;
        gson = new Gson();
    }

    /**
     * 积分领取
     * @param url
     * @param openId
     */
    public void chipDraw(String url, String openId,String token) {
        String[] split = url.split("\\?")[1].split("&");
        String nfcCode = "";
        String belong2Us = DealUrlDataUtils.isBelong2Us(url);
        for (String data: split) {
            if (data.contains(belong2Us)) {
                nfcCode = data.replace(belong2Us, "");
                break;
            }
        }
        if(TextUtils.isEmpty(nfcCode)) return;
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        if(!TextUtils.isEmpty(openId)) params.put("openId",openId);
        params.put("p",nfcCode);
        if(!TextUtils.isEmpty(token)) params.put("token",token);
        new Request(ApiConfig.API_CHIP_DRAW,params){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("AppraisalModel",json.toString());
                    final ChipDrawResultBean chipDrawResultBean = gson.fromJson(json.toString(),ChipDrawResultBean.class);
                    final int code = chipDrawResultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (200 == code) {
                                iView.chipDrawSuccess(chipDrawResultBean.getRecord());
                            }else if(code == BaseException.RE_LOGIN){
                                iView.reLogin();
                            }else{
                                iView.onLoadFail(chipDrawResultBean.getMsg());
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

    /**
     * 绑定手机号
     * @param openId
     * @param phone
     * @param code
     */
    public void authMobileBinding(String openId,String token, final String phone, String code) {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("code",code);
        params.put("mobile",phone);
        params.put("openId",openId);
        params.put("token",token);
        params.put("type","1");
        new Request(ApiConfig.API_auth,params){
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
                                iView.authMobileBindingSuccess(phone);
                            }else if(code == BaseException.RE_LOGIN){
                                iView.reLogin();
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

    /**
     * 登录
     * @param mobile
     * @param code
     */
    public void login(String mobile, String code) {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("code",code);
        params.put("mobile",mobile);
        new Request(ApiConfig.API_login,params){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("AppraisalModel",json.toString());
                    final LoginResultBean resultBean = gson.fromJson(json.toString(),LoginResultBean.class);
                    final int code = resultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
     * @param callBack
     */
    public void sendCode(String phone, String type, final LoginDialog.SendSmsCallBack callBack) {
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
                                callBack.sendSmsSuccess();
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

    interface IView {
        void chipDrawSuccess(ChipDrawResultBean.Entity entity);
        void onLoadFail(String errorMessage);
        void reLogin();
        void loginSuccess(LoginResultBean.Entity entity);
        void authMobileBindingSuccess(String mobile);
    }
}
