package com.xm6leefun.scan_lib.net;

import android.app.Application;

import com.xm6leefun.scan_lib.login.bean.LoginResultBean;
import com.xm6leefun.scan_lib.utils.ChipConstant;
import com.xm6leefun.scan_lib.utils.SharePreferenceUtil;
import com.xm6leefun.scan_lib.utils.StrUtils;

import java.util.LinkedHashMap;

public class ApiConfig {
    public static String IPs;
    public static String API_identifyData;
    public static String API_identifyLink;
    public static String API_CHIP_DRAW;
    public static String API_auth;//授权绑定
    public static String API_login;//登录
    public static String API_PSW_login;//登录
    public static String API_SenCode;//验证码
    public static String URL_HEAD_CHIP;
    public static String TRACE_H5_URL;
    public static String URL_HEAD_QRCODE;
    public static final String URL_HEAD_CHIP_TRACE = "http://trace.xm6leefun.com/origin/html/origin.html";
    // 隐私政策
    public static final String PRIVACY_POLICY_URL = "http://12315.weecot.com/pirvacyPolicy.html";
    // 用户协议
    public static final String USER_PROTOCOL_URL = "http://12315.weecot.com/userAgreement.html";

    private static ApiConfig  INSTANCE = null;
    public static ApiConfig getInstance() {
        if(INSTANCE == null){
            INSTANCE = new ApiConfig();
        }
        return INSTANCE;
    }


    private static Application mApplicationContext;
    /**
     * 初始化
     * @param isDebug
     */
    public static ApiConfig init(boolean isDebug,Application application) {
        mApplicationContext = application;
        String IP;
        if(isDebug) IP = "http://testsercviceapi.weecot.com:9086/v_1_0_1/";
        else IP = "http://sercviceapi.weecot.com:9086/v_1_0_1/";

        if(isDebug) TRACE_H5_URL = "http://testchip.weecot.com/#/";
        else TRACE_H5_URL = "http://chip.weecot.com/#/";
        if(isDebug) URL_HEAD_QRCODE = "http://test12315.weecot.com";
        else URL_HEAD_QRCODE = "http://12315.weecot.com";

        if(isDebug) API_CHIP_DRAW = "http://testchip.weecot.com:9082/v_1_0_1/Integral/chipDraw";
        else API_CHIP_DRAW = "http://chip.weecot.com:9082/v_1_0_1/Integral/chipDraw";

        if(isDebug) URL_HEAD_CHIP = "http://testchip.weecot.com";
        else URL_HEAD_CHIP = "http://chip.weecot.com";

        IPs=IP;
        API_identifyData = IP + "PdaInfo/identifyData";
        API_identifyLink = IP + "PdaInfo/identifyLink";
        API_auth = IP + "userInfo/impowerEide";
        API_login = IP + "userInfo/userLogin";
        API_PSW_login = IP + "userInfo/userPassLogin";
        API_SenCode = IP + "userInfo/getNoteCode";
        return getInstance();
    }

    public static Application getApplication(){
        return mApplicationContext;
    }

    public static LinkedHashMap<String,Object> identifyData(String num,String goodsId,String nfcUid,String nfcCode,int status){
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        map.put("num",num);
        map.put("goodsId",goodsId);
        map.put("nfcUid",nfcUid);
        map.put("nfcCode",nfcCode);
        map.put("status",status);
        return map;
    }

    public static LinkedHashMap<String,Object> getIdentifyData(String userId, String nfcUid, String nfcCode, int breakStatus){
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        map.put("userId",userId);
        map.put("nfcUid",nfcUid);
        map.put("p",nfcCode);
        map.put("status",breakStatus);
        return map;
    }

    //芯片鉴定详情接口  用于新版芯片2.0
    public static LinkedHashMap getIdentifyDataNew(String nfcUid, String t, String v, String d) {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        map.put("nfcUid",nfcUid);
        map.put("t",t);
        map.put("v",v);
        map.put("d",d);
        map.put("status",0);
        return map;
    }

    public static LinkedHashMap identifyLink(String chipLink) {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        map.put("chipLink",chipLink);
        return map;
    }

    /**
     * 记录登录信息
     * @param userInfoBean
     */
    public void saveLoginData(LoginResultBean.Entity.UserInfoBean userInfoBean){
        if(userInfoBean == null)return;
        SharePreferenceUtil.setString(ChipConstant.User_ID,userInfoBean.getUserId());
        SharePreferenceUtil.setString(ChipConstant.LOGIN_ID,userInfoBean.getId());
        SharePreferenceUtil.setString(ChipConstant.HEAD_PORTRAIT,userInfoBean.getHeadPortrait());
        String mobile = userInfoBean.getMobile();
        if(!StrUtils.isEmpty(mobile) && !"0".equals(mobile)) {
            SharePreferenceUtil.setString(ChipConstant.MOBILE, mobile);
        }
        SharePreferenceUtil.setString(ChipConstant.TOKEN,userInfoBean.getToken());
        SharePreferenceUtil.setString(ChipConstant.NICK_NAME,userInfoBean.getNickName());
        SharePreferenceUtil.setString(ChipConstant.WX_NAME,userInfoBean.getWxName());
        SharePreferenceUtil.setString(ChipConstant.USER_UUID,userInfoBean.getUserUuid());
        SharePreferenceUtil.setString(ChipConstant.OPEN_ID,userInfoBean.getOpenId());
        SharePreferenceUtil.setInt(ChipConstant.IS_SETPWD,userInfoBean.getIsSetPwd());
    }
    /**
     * 移除登录信息
     */
    public void removeLoginData(){
        SharePreferenceUtil.removeByKey(ChipConstant.User_ID);
        SharePreferenceUtil.removeByKey(ChipConstant.LOGIN_ID);
        SharePreferenceUtil.removeByKey(ChipConstant.HEAD_PORTRAIT);
        SharePreferenceUtil.removeByKey(ChipConstant.MOBILE);
        SharePreferenceUtil.removeByKey(ChipConstant.TOKEN);
        SharePreferenceUtil.removeByKey(ChipConstant.NICK_NAME);
        SharePreferenceUtil.removeByKey(ChipConstant.WX_NAME);
        SharePreferenceUtil.removeByKey(ChipConstant.USER_UUID);
        SharePreferenceUtil.removeByKey(ChipConstant.OPEN_ID);
        SharePreferenceUtil.removeByKey(ChipConstant.UNION_ID);
        SharePreferenceUtil.removeByKey(ChipConstant.IS_SETPWD);
    }
}
