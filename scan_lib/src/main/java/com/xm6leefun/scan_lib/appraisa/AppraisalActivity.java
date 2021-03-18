package com.xm6leefun.scan_lib.appraisa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.WebApiActivity;
import com.xm6leefun.scan_lib.appraisa.bean.ChipDrawResultBean;
import com.xm6leefun.scan_lib.dialog.GetPointsBindDialog;
import com.xm6leefun.scan_lib.dialog.GetPointsDialog;
import com.xm6leefun.scan_lib.dialog.LoginDialog;
import com.xm6leefun.scan_lib.dialog.PointsActivityDialog;
import com.xm6leefun.scan_lib.dialog.TipsDialog;
import com.xm6leefun.scan_lib.listener.AuthListener;
import com.xm6leefun.scan_lib.login.ZWDLoginActivity;
import com.xm6leefun.scan_lib.login.bean.LoginResultBean;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.nfc.bean.AppraisalDataBean;
import com.xm6leefun.scan_lib.utils.ChipConstant;
import com.xm6leefun.scan_lib.utils.SharePreferenceUtil;
import com.xm6leefun.scan_lib.utils.StrUtils;
import com.xm6leefun.scan_lib.weight.DragFloatActionButton;

import java.util.HashMap;

import static com.xm6leefun.scan_lib.utils.DealUrlDataUtils.getTVD;

/**
 * @Description: nfc鉴定页
 * @Author: hhh
 * @CreateDate: 2021/3/4 16:06
 */
public class AppraisalActivity extends Activity implements AppraisalModel.IView{
    public static final String DATA = "data";
    private TextView appTvTtstate;
    private TextView appTvTtstateDetails;
    private TextView appBtnLookDetails;
    private TextView mTvNotOurs;
    private ImageView mIv;
    private ImageView mIvStatus;
    private RelativeLayout mRlImage;
    private RelativeLayout mIvJianding;
    private DragFloatActionButton appIvActivityEntry;

    private String openId;
    private String url;
    private AppraisalModel appraisalModel;
    private String token;

    public static void jump(Context context,Bundle args){
        Intent intent = new Intent(context,AppraisalActivity.class);
        if(args != null)
            intent.putExtras(args);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appraisal_layout);
        appraisalModel = new AppraisalModel(this,this);
        initView();
        initData();
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.base_topBar_tv_title);
        tvTitle.setText(R.string.nfc_jianding_rsult);
        appTvTtstate = findViewById(R.id.app_tv_ttstate);
        appTvTtstateDetails = findViewById(R.id.app_tv_ttstate_details);
        appBtnLookDetails = findViewById(R.id.app_btn_look_details);
        mTvNotOurs = findViewById(R.id.app_tv_not_ours);
        mIv = findViewById(R.id.app_iv_ttstate);
        mIvStatus = findViewById(R.id.app_iv_state);
        mRlImage = findViewById(R.id.app_rl_image);
        mIvJianding = findViewById(R.id.guideline);
        appIvActivityEntry = findViewById(R.id.app_iv_activity_entry);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String istt = null,isTrueStatus = null,openStatus = null,breakStatus = null,goodsName = null,goodsLogo = null;
        boolean isActivity = false,integralStatus = false;
        int activateState = 0;
        if(bundle != null){
            AppraisalDataBean appraisalDataBean = bundle.getParcelable(DATA);
            if(appraisalDataBean != null) {
                url = appraisalDataBean.getUrl();
                istt = appraisalDataBean.getIstt();
                isTrueStatus = appraisalDataBean.getIsTrueStatus();
                openStatus = appraisalDataBean.getOpenStatus();
                breakStatus = appraisalDataBean.getBreakStatus();
                isActivity = appraisalDataBean.isActivity();
                integralStatus = appraisalDataBean.isIntegralStatus();
                activateState = appraisalDataBean.getActivateState();
                goodsName = appraisalDataBean.getGoodsName();
                goodsLogo = appraisalDataBean.getGoodsLogo();
            }
        }
        handleChipData(istt, isTrueStatus, openStatus, breakStatus, url, isActivity, integralStatus,activateState,goodsName,goodsLogo);
    }

    /**
     * 处理芯片数据
     */
    private void handleChipData(String istt,
                                String isTrueStatus,
                                String openStatus,
                                String breakStatus,
                                String url,
                                boolean isActivity,
                                boolean integralStatus,
                                int activateState,
                                String goodsName,
                                String goodsLogo) {
        boolean isOpened = false;// 芯片是否断裂
        //是否普通芯片
        if (!StrUtils.isEmpty(istt) && !StrUtils.isEmpty(url)) {// 芯片类型以及连接不为空
            if (istt.equals(ChipConstant.NORMAL)) {
                if (!"200".equals(isTrueStatus)) {  // 假
                    String msg = getResources().getString(R.string.no_goods);
                    appTvTtstate.setText(msg);
                    mIvStatus.setImageResource(R.mipmap.identify_img_failure);
                    mRlImage.setVisibility(View.GONE);
                    appTvTtstateDetails.setVisibility(View.GONE);
                    mTvNotOurs.setVisibility(View.VISIBLE);
                    appBtnLookDetails.setVisibility(View.GONE);
                } else {
                    mIvStatus.setImageResource(R.mipmap.identify_img_certificate);
                    mRlImage.setVisibility(View.GONE);
                    appTvTtstateDetails.setVisibility(View.GONE);
                    mTvNotOurs.setVisibility(View.GONE);
                    appBtnLookDetails.setVisibility(View.VISIBLE);
                }

            } else if ((istt.equals(ChipConstant.TT))) {
                String msgTT = null;
                if ("200".equals(isTrueStatus)) {  // 真
                    mIvStatus.setImageResource(R.mipmap.identify_img_certificate);
                    if (openStatus.equals("67")) {
                        isOpened = false;
                        msgTT = getResources().getString(R.string.no_open);
                        mIv.setImageResource(R.mipmap.identify_ico_lock);
                        appTvTtstateDetails.setVisibility(View.GONE);
                    } else if (openStatus.equals("79")) {
                        isOpened = true;
                        msgTT = getResources().getString(R.string.already_open);
                        mIv.setImageResource(R.mipmap.identify_ico_unlock);
                        appTvTtstateDetails.setVisibility(View.VISIBLE);
                        appTvTtstateDetails.setText(getResources().getString(R.string.nfc_zhu));
                    } else if (openStatus.equals("73")) {
                        isOpened = false;
                        msgTT = getResources().getString(R.string.no_goods);
                        mIvJianding.setVisibility(View.GONE);
                        appBtnLookDetails.setVisibility(View.GONE);
                        mIv.setVisibility(View.GONE);
                    }
                } else {
                    mIvStatus.setImageResource(R.mipmap.identify_img_failure);
                    msgTT = getResources().getString(R.string.no_goods);
                    mRlImage.setVisibility(View.GONE);
                    appBtnLookDetails.setVisibility(View.GONE);
                    mIv.setVisibility(View.GONE);
                }
                appTvTtstate.setText(msgTT);
            } else if (istt.equals(ChipConstant.NTAG_424_DNA_TT) || istt.equals(ChipConstant.NTAG_WD5502)) {// 新版NTAG424DNATT芯片/WD5502/帝尊台
                String msg;
                if (!StrUtils.isEmpty(breakStatus)) {
                    mIvStatus.setImageResource(R.mipmap.identify_img_certificate);
                    if (breakStatus.equals("1")) {  // 断开
                        isOpened = true;
                        msg = getResources().getString(R.string.already_open);
                        mIv.setImageResource(R.mipmap.identify_ico_unlock);
                        appTvTtstateDetails.setVisibility(View.VISIBLE);
                        appTvTtstateDetails.setText(getResources().getString(R.string.nfc_zhu));
                    } else {  // 未断开
                        isOpened = false;
                        msg = getResources().getString(R.string.no_open);
                        mIv.setImageResource(R.mipmap.identify_ico_lock);
                        appTvTtstateDetails.setVisibility(View.GONE);
                    }
                } else {
                    msg = getResources().getString(R.string.no_goods);
                    mIvStatus.setImageResource(R.mipmap.identify_img_failure);
                    mRlImage.setVisibility(View.GONE);
                    appTvTtstate.setText(msg);
                    appTvTtstateDetails.setVisibility(View.GONE);
                    mTvNotOurs.setVisibility(View.VISIBLE);
                    appBtnLookDetails.setVisibility(View.GONE);
                }
                appTvTtstate.setText(msg);
            }
        } else {  // 空芯片
            String msg = getResources().getString(R.string.no_goods);
            mIvStatus.setImageResource(R.mipmap.identify_img_failure);
            mRlImage.setVisibility(View.GONE);
            appTvTtstate.setText(msg);
            appTvTtstateDetails.setVisibility(View.GONE);
            mTvNotOurs.setVisibility(View.VISIBLE);
            appBtnLookDetails.setVisibility(View.GONE);
        }
        // 判断活动入口是否开启
//        appIvActivityEntry.setVisibility(isActivity ? View.VISIBLE : View.GONE);
        if (isActivity) {
            switch (activateState) {
                case 1:  // 活动未开始
                    appIvActivityEntry.setVisibility(View.GONE);
                    break;
                case 2:  // 活动进行中
                    if (isOpened) {//芯片断裂
                        if (!integralStatus) {  // 积分未被领取才显示弹框
                            if (!StrUtils.isEmpty(url)) {
                                requestPointsReceive(url);  // 请求积分领取接口
                            }
                        } else {  // 积分已被领取，显示活动缩略图
                            appIvActivityEntry.setVisibility(View.VISIBLE);
                        }
                    } else {//芯片未断裂
                        if (!StrUtils.isEmpty(url)) {
                            showActivityDialog(activateState,goodsName,goodsLogo);
                        }
                    }
                    break;
                case 3:  // 活动已结束
                case 4:  // 活动已关闭
                    if (!StrUtils.isEmpty(url)) {
                        showActivityDialog(activateState,goodsName,goodsLogo);
                    }
                    break;
            }
        }
    }

    /**
     * 查看商品详情
     * @param view
     */
    public void openDetails(View view){
        if (!StrUtils.isEmpty(url)) {
            String newUrl = "";
            if (url.startsWith(ApiConfig.URL_HEAD_CHIP)) {
                if (!url.contains("token=")) {
                    newUrl = url + "&type=2&token=" + SharePreferenceUtil.getString(ChipConstant.TOKEN);
                } else {
                    newUrl = url + "&type=2";
                }
                WebApiActivity.jump(this,newUrl);
            } else {
                if (!url.contains("token=")) {
                    newUrl = url + "&token=" + SharePreferenceUtil.getString(ChipConstant.TOKEN);
                }
                WebApiActivity.jump(this,newUrl);
            }
        } else {
            Toast.makeText(getApplicationContext(),"Error code: 0",Toast.LENGTH_SHORT).show();
        }
    }

    private void showActivityDialog(int activateState,String goodsName,String goodsLogo) {
        PointsActivityDialog pointsActivityDialog = new PointsActivityDialog.Builder()
                .setActivateState(activateState)
                .setGoodsName(goodsName)
                .setGoodsLogo(goodsLogo)
                .setClickListener(new PointsActivityDialog.ClickListener() {
                    @Override
                    public void onShow() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onReadDetails() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                        // todo 跳转活动详情
                        goH5(url);
                    }
                }).build();
        pointsActivityDialog.show(getFragmentManager(), PointsActivityDialog.TAG);
    }

    private void goH5(String url) {
        String urls = getUrl(url);
        if (!StrUtils.isEmpty(urls)) {
            if (!urls.contains("token=")) {
                urls = urls + "&token=" + SharePreferenceUtil.getString(ChipConstant.TOKEN);
            }
            if(!urls.contains("type=")){
                urls = urls + "&type=2";
            }
            WebApiActivity.jump(AppraisalActivity.this,urls);
        }
    }

    private String getUrl(String url) {
        String token = SharePreferenceUtil.getString(ChipConstant.TOKEN);
        String urls = "";
        String p = "";
        if (url.contains("p=")) {
            String[] split = url.split("\\?");
            if (split.length >= 2) {
                String[] str = split[1].split("&");
                for (String s : str) {
                    if (s.contains("p=")) {
                        p = s.replace("p=", "");
                        break;
                    }
                }
            }
            if (!StrUtils.isEmpty(p)) {
                urls = ApiConfig.TRACE_H5_URL+ "integral/receive?p=" + p + "&d=&t=&v=&token=" + token;
            }
        } else if (url.contains("t=") && url.contains("v=") && url.contains("d=")) {  // t、v、d
            HashMap<String, String> hashMap = getStr(url);
            if (hashMap != null) {
                urls = ApiConfig.TRACE_H5_URL+ "integral/receive?p=" + "&d=" + hashMap.get("d") + "&t=" + hashMap.get("t") + "&v=" + hashMap.get("v") + "&token=" + token;
            }
        }
        return urls;
    }

    private HashMap<String, String> getStr(String url) {
        HashMap<String, String> mDAndK = getTVD(url);
        if (mDAndK.size() > 0) {
            String t = mDAndK.get("t");
            String v = mDAndK.get("v");
            String d = mDAndK.get("d");
            Log.d("","获取的参数t: " + t + "\n获取的参数v: " + v + "\n获取的参数d: " + d);
            t = StrUtils.isEmpty(t) ? "" : t;
            v = StrUtils.isEmpty(v) ? "" : v;
            d = StrUtils.isEmpty(d) ? "" : d;
            HashMap<String, String> newDAndK = new HashMap<>();
            newDAndK.put("t", t);
            newDAndK.put("v", v);
            newDAndK.put("d", d);
            return newDAndK;
        }
        return null;
    }

    /**
     * 领取积分
     * @param url
     */
    private void requestPointsReceive(String url) {
        if (!StrUtils.isEmpty(url) && (url.contains("p=") || url.contains("nfcCode="))){
            openId = SharePreferenceUtil.getString(ChipConstant.OPEN_ID);
            token = SharePreferenceUtil.getString(ChipConstant.TOKEN);
            appraisalModel.chipDraw(url,openId,token);
        }
    }

    @Override
    public void chipDrawSuccess(ChipDrawResultBean.Entity entity) {
        switch (entity.getIntegralStatus()) {  // 积分领取状态：1获得积分未绑定手机；2没有获得积分；3领取成功；4已领取；5，登录领取积分
            case 1:  // 获得积分未绑定手机，提示去登陆或者绑定手机号
                if (!StrUtils.isEmpty(url)) {
                    showNoBindDialog(entity.getNum(), url);
                }
                break;
            case 2:  // 没有获得积分
                showTipsDialog(getString(R.string.identify_dialog_no_points));
                break;
            case 3:  // 领取成功
                if (!StrUtils.isEmpty(url)) {
                    showReceiveSuccessDialog(entity.getNum(), true, url);
                }
                break;
            case 4:  // 已领取
                showTipsDialog(getString(R.string.identify_dialog_already_received));
                break;
            case 5:  // 登录领取积分
                if (!StrUtils.isEmpty(url)) {
                    showReceiveSuccessDialog(entity.getNum(), false, url);
                }
                break;
        }
    }

    @Override
    public void onLoadFail(String errorMessage) {
        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
    }

    /**
     * token过期需要重新登录回调
     */
    @Override
    public void reLogin() {
        //打开登录界面，并监听登录结果
        ZWDLoginActivity.jumpForData(this, new AuthListener() {
            @Override
            public void onData(String resultJson) {
                requestPointsReceive(url);  // 登录成功后再次请求领取积分接口
            }
        });
    }

    @Override
    public void loginSuccess(LoginResultBean.Entity entity) {
        ApiConfig.getInstance().saveLoginData(entity.getUserInfo());
        requestPointsReceive(url);  // 登录成功后再次请求领取积分接口
    }

    @Override
    public void authMobileBindingSuccess(String mobile) {
        SharePreferenceUtil.setString(ChipConstant.MOBILE,mobile);
        requestPointsReceive(url);  // 登录成功后再次请求领取积分接口
    }

    /**
     * 领取成功
     * @param num
     * @param isLoginBack
     * @param url
     */
    private void showReceiveSuccessDialog(long num, boolean isLoginBack, final String url) {
        String title = "";
        // 判断是否从登陆而来
        if (isLoginBack) {
            title = getString(R.string.identify_dialog_get_points, String.valueOf(num));
        } else {
            title = getString(R.string.identify_dialog_get_points_yet, String.valueOf(num));
        }
        GetPointsDialog getPointsDialog = new GetPointsDialog.Builder().setTitle(title)
                .setClickListener(new GetPointsDialog.ClickListener() {
                    @Override
                    public void readPointsDetails() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                        String url = ApiConfig.TRACE_H5_URL + "personal/origin?token=" + token+"&type=2";
                        WebApiActivity.jump(AppraisalActivity.this,url);
                    }

                    @Override
                    public void readActivityDetails() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                        // todo 跳转活动详情
                        goH5(url);
                    }
                }).build();
        getPointsDialog.show(getFragmentManager(), GetPointsDialog.TAG);
    }

    /**
     * 显示错误提示
     * @param tips
     */
    private void showTipsDialog(String tips) {
        new TipsDialog.Builder()
                .setIsCenterGravity(true)
                .setContent(tips)
                .setClickListener(new TipsDialog.ClickListener() {
                    @Override
                    public void onSure() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                    }
                })
                .build().show(getFragmentManager(), TipsDialog.TAG);
    }

    /**
     * 获取积分弹框（需绑定个人账户）
     * @param num
     * @param url
     */
    private void showNoBindDialog(long num, final String url) {
        GetPointsBindDialog getPointsBindDialog = new GetPointsBindDialog.Builder().setPoints(num)
                .setClickListener(new GetPointsBindDialog.ClickListener() {
                    @Override
                    public void readDetails() {
                        //跳转积分详情
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                        goH5(url);
                    }

                    @Override
                    public void bindPhone() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                        LoginDialog bindForPointsDialog = new LoginDialog.Builder().setTitle(getString(R.string.identify_dialog_login)).setClickListener(new LoginDialog.ClickListener() {
                            @Override
                            public void onSure(String phone, String code) {
                                //如果微信的openId不为空，则进行手机号和微信号绑定，否则进行手机号登录
                                if (!StrUtils.isEmpty(openId)) {
                                    appraisalModel.authMobileBinding(openId,token,phone,code);
                                } else {
                                    appraisalModel.login(phone, code);
                                }
                            }

                            @Override
                            public void sendSms(String phone, LoginDialog.SendSmsCallBack callBack) {
                                //发送验证码
                                appraisalModel.sendCode(phone,"userLogin",callBack);
                            }
                        }).build();
                        bindForPointsDialog.show(AppraisalActivity.this.getFragmentManager(), LoginDialog.TAG);
                    }

                    @Override
                    public void showActivity() {
                        appIvActivityEntry.setVisibility(View.VISIBLE);
                    }
                }).build();
        getPointsBindDialog.show(getFragmentManager(), GetPointsBindDialog.TAG);
    }

    /**
     * 积分活动入口
     * @param view
     */
    public void activityEntry(View view){
        goH5(url);
    }
}
