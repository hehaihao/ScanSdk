package com.xm6leefun.scan_lib.login;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.WebApiActivity;
import com.xm6leefun.scan_lib.ZWDAuth;
import com.xm6leefun.scan_lib.listener.AuthListener;
import com.xm6leefun.scan_lib.login.bean.LoginResultBean;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.utils.StrUtils;
import com.xm6leefun.scan_lib.weight.svprogress.SVProgressHUD;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/8 10:08
 */
public class ZWDLoginActivity extends Activity implements View.OnClickListener, CodeLoginFragment.OnContentListener,LoginModel.IView {
    private FragmentManager manager;
    private CodeLoginFragment codeLoginFragment;
    private PwdLoginFragment pwdLoginFragment;
    private LoginModel loginModel;
    private LinearLayout topBarLinMain;
    private ImageView topBarIvBack;

    public static void jump(Context context){
        Intent intent = new Intent(context, ZWDLoginActivity.class);
        context.startActivity(intent);
    }
    public static void jumpForData(Context context, AuthListener listener){
        ZWDAuth.getInstance().setZWDAuthListener(listener);
        Intent intent = new Intent(context, ZWDLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        //获取管理
        manager = getFragmentManager();
        loginModel = new LoginModel(this,this);
        //一旦切换账号，则清除用户信息
        loginModel.clearUserData();
        topBarLinMain = findViewById(R.id.base_topBar_lin_main);
        topBarIvBack = findViewById(R.id.base_topBar_iv_back);
        topBarLinMain.setBackgroundColor(getResources().getColor(R.color.white));
        topBarIvBack.setImageResource(R.mipmap.back_icon);
        //单选按钮点击事件
        findViewById(R.id.code_login).setOnClickListener(this);
        findViewById(R.id.pwd_login).setOnClickListener(this);

        //单选按钮默认选中第一个
        RadioGroup mGroup = findViewById(R.id.radiogroup);
        mGroup.check(R.id.code_login);
        //默认显示第一个界面
        selectFragment(0);
    }

    private Fragment mFragment = null;//当前选中
    private void selectFragment(int index) {
        //开启一个Fragment事务
        FragmentTransaction transaction = manager.beginTransaction();
        //隐藏原先的Fragment
        hideFragment(transaction);
        switch (index) {
            case 0:
                //fragment判断是否为空
                if (codeLoginFragment == null) {
                    //为空则实例化
                    codeLoginFragment = CodeLoginFragment.getInstance();
                    //添加
                    transaction.add(R.id.fram_login, codeLoginFragment);
                } else {
                    //不为空则显示
                    transaction.show(codeLoginFragment);
                }
                mFragment = codeLoginFragment;
                break;
            case 1:
                if (pwdLoginFragment == null) {
                    pwdLoginFragment = PwdLoginFragment.getInstance();
                    transaction.add(R.id.fram_login, pwdLoginFragment);
                } else {
                    transaction.show(pwdLoginFragment);
                }
                mFragment = pwdLoginFragment;
                break;
        }

        //提交事务
        transaction.commit();
    }

    /**
     * 只要fragment不为空，便一律隐藏
     */
    private void hideFragment(FragmentTransaction transaction){
        if (codeLoginFragment != null) {
            transaction.hide(codeLoginFragment);
        }
        if (pwdLoginFragment != null) {
            transaction.hide(pwdLoginFragment);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.code_login) {
            selectFragment(0);
        } else if (id == R.id.pwd_login) {
            selectFragment(1);
        }
    }

    /**
     * 关闭当前界面
     * @param view
     */
    public void close(View view) {
        finish();
    }
    /**
     * 登录
     */
    public void login(View view) {
        if (mFragment instanceof CodeLoginFragment) {  // 验证码登录
            CodeLoginFragment codeLoginFragment = (CodeLoginFragment) mFragment;
            String phone = codeLoginFragment.getPhone();
            String code = codeLoginFragment.getCode();
            if (StrUtils.isEmpty(phone)) {
                Toast.makeText(getApplicationContext(),getString(R.string.phone_is_null),Toast.LENGTH_SHORT).show();
                return;
            }
            if (StrUtils.isEmpty(code)) {
                Toast.makeText(getApplicationContext(),getString(R.string.code_not_empty),Toast.LENGTH_SHORT).show();
                return;
            }
            loginModel.codeLogin(phone, code);
        } else if(mFragment instanceof PwdLoginFragment){  // 密码登录
            PwdLoginFragment pwdLoginFragment = (PwdLoginFragment) mFragment;
            String phone = pwdLoginFragment.getPhone();
            String pws = pwdLoginFragment.getPwd();
            if (StrUtils.isEmpty(phone)) {
                Toast.makeText(getApplicationContext(),getString(R.string.phone_is_null),Toast.LENGTH_SHORT).show();
                return;
            }
            if (StrUtils.isEmpty(pws)) {
                Toast.makeText(getApplicationContext(),getString(R.string.pwd_not_empty),Toast.LENGTH_SHORT).show();
                return;
            }
            loginModel.pwdLogin(phone, pws);
        }
    }

    /**
     * 隐私政策
     * @param view
     */
    public void privacyPolicy(View view){
        WebApiActivity.jump(this,ApiConfig.PRIVACY_POLICY_URL);
    }

    /**
     * 服务协议
     * @param view
     */
    public void userProtocol(View view){
        WebApiActivity.jump(this,ApiConfig.USER_PROTOCOL_URL);
    }

    @Override
    public void sendSms(String phone, CodeLoginFragment.SendSmsCallBack callBack) {
        loginModel.sendCode(phone,"userRegister",callBack);
    }

    private SVProgressHUD svProgressHUD;
    @Override
    public void loading(String s) {
        if(svProgressHUD == null)
            svProgressHUD = new SVProgressHUD(this);
        if(!svProgressHUD.isShowing()){
            svProgressHUD.showWithStatus(s);
        }
    }

    @Override
    public void dismiss() {
        if(svProgressHUD.isShowing()){
            svProgressHUD.dismiss();
        }
    }

    @Override
    public void loginSuccess(LoginResultBean.Entity entity) {
        ApiConfig.getInstance().saveLoginData(entity.getUserInfo());
        ZWDAuth.getInstance().onData("nickName："+entity.getUserInfo().getToken());
        finish();
    }

    @Override
    public void onLoadFail(String errorMessage) {
        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reLogin() {

    }
}
