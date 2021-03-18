package com.xm6leefun.scan_lib.nfc;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.WebApiActivity;
import com.xm6leefun.scan_lib.appraisa.AppraisalActivity;
import com.xm6leefun.scan_lib.dialog.NotPlatformTipsDialog;
import com.xm6leefun.scan_lib.dialog.TwoButtonTipsDialog;
import com.xm6leefun.scan_lib.nfc.bean.AppraisalDataBean;
import com.xm6leefun.scan_lib.nfc.bean.ChipResultBean;
import com.xm6leefun.scan_lib.nfc.bean.IdentifyDataResultBean;
import com.xm6leefun.scan_lib.utils.ChipConstant;
import com.xm6leefun.scan_lib.weight.svprogress.SVProgressHUD;

import java.util.List;

import static com.xm6leefun.scan_lib.exception.BaseException.NETWORK_ERR_CODE;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/3 13:58
 */
public class NfcActivity extends Activity implements NfcModel.IView {
    private NfcAdapter mNfcAdapter;
    private LinearLayout new_main_lin_nfc_not_support;
    private LinearLayout newMainLinNfcSet;

    private NfcModel nfcModel;
    protected FragmentManager mFragmentManager;

    public static void jump(Context context){
        Intent intent = new Intent(context, NfcActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_layout);
        mFragmentManager = getFragmentManager();
        nfcModel = new NfcModel(this);
        new_main_lin_nfc_not_support = findViewById(R.id.new_main_lin_nfc_not_support);
        newMainLinNfcSet = findViewById(R.id.new_main_lin_nfc_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNfcAdapter();
        if (mNfcAdapter != null) {
            //一旦截获NFC消息，就会通过PendingIntent调用窗口
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            //用于打开前台调度（拥有最高的权限），当这个Activity位于前台（前台进程），即可调用这个方法开启前台调度
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    private void initNfcAdapter() {
        //获取NfcAdapter对象，此方法与获取蓝牙适配器对象类似
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (mNfcAdapter == null) {
            new_main_lin_nfc_not_support.setVisibility(View.VISIBLE);
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            newMainLinNfcSet.setVisibility(View.VISIBLE);
        } else {
            newMainLinNfcSet.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //调用disableForegroundDispatch方法关闭前台调度
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //处理nfc
        nfcModel.handleNFCIntent(this, intent);
    }

    /**
     * 设置nfc
     * @param view
     */
    public void nfcSetting(View view){
        startActivity(new Intent("android.settings.NFC_SETTINGS"));
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
    public void getIdentifyLinkSuccess(ChipResultBean.Entity entity, String url, String isNormal) {
        showBelongUsDialog(entity.getCompanyName(), isNormal, url);
    }

    /**
     * 显示属于第三方芯片弹框
     * @param companyName
     * @param isNormal
     */
    private void showBelongUsDialog(String companyName, final String isNormal, final String url) {
        new TwoButtonTipsDialog.Builder()
                .setTitle(getString(R.string.reminder))
                .setContent(getString(R.string.no_our_platform, companyName))
                .setSureText(getString(R.string.continue_read))
                .setIsCenterGravity(false)
                .setClickListener(new TwoButtonTipsDialog.ClickListener() {
                    @Override
                    public void onSure() {
                        if (ChipConstant.TT.equals(isNormal)) {
                            Bundle bundle = new Bundle();
                            AppraisalDataBean dataBean = new AppraisalDataBean();
                            dataBean.setIstt(isNormal);
                            dataBean.setUrl(url);
                            AppraisalActivity.jump(NfcActivity.this,bundle);
                        } else {//打开h5
                            WebApiActivity.jump(NfcActivity.this,url);
                        }

                    }
                }).build().show(mFragmentManager, TwoButtonTipsDialog.TAG);
    }

    @Override
    public void getIdentifyLinkFail(int code,String errorMessage) {
        if (code == NETWORK_ERR_CODE) {//网络错误，提示
            Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
        } else {
            AppraisalActivity.jump(this,null);
        }
    }

    @Override
    public void dealFakeNfc() {
        AppraisalActivity.jump(this,null);
    }

    @Override
    public void getIdentifyDataFail(int code,String msg, String isNormal) {
        if (code == NETWORK_ERR_CODE) {//网络错误，提示
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            AppraisalDataBean dataBean = new AppraisalDataBean();
            dataBean.setIstt(isNormal);
            AppraisalActivity.jump(this,bundle);
        }
    }

    @Override
    public void getIdentifyDataNewSuccess(List<IdentifyDataResultBean.IdentifyDataResultListBean> identifyDataResultBeans, String url, String isNormal, String openStatus) {
        IdentifyDataResultBean.IdentifyDataResultListBean bean = identifyDataResultBeans.get(0);
        if (bean != null) {
            //跳转鉴定页
            Bundle bundle = new Bundle();
            AppraisalDataBean dataBean = new AppraisalDataBean();
            dataBean.setOpenStatus(openStatus);
            dataBean.setIstt(isNormal);
            dataBean.setUrl(url);
            dataBean.setIsTrueStatus("200");
            dataBean.setBreakStatus(bean.isStatus() ? "1" : "2");
            dataBean.setIsActivity(bean.isIsActivity());
            dataBean.setIntegralStatus(bean.isIntegralStatus());
            dataBean.setActivateState(bean.getActivateState());
            dataBean.setGoodsName(bean.getGoodsName());
            dataBean.setGoodsLogo(bean.getGoodsLogo());
            bundle.putParcelable(AppraisalActivity.DATA, dataBean);
            AppraisalActivity.jump(this,bundle);
        }
    }

    private NotPlatformTipsDialog notPlatformTipsDialog = null;
    @Override
    public void notPlatformTips() {
        if (notPlatformTipsDialog == null)
            notPlatformTipsDialog = new NotPlatformTipsDialog.Builder().build();
        if (!notPlatformTipsDialog.isAdded())
            notPlatformTipsDialog.show(mFragmentManager, NotPlatformTipsDialog.TAG);
    }
}
