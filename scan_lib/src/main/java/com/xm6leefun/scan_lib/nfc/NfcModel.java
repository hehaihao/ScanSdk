package com.xm6leefun.scan_lib.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.exception.BaseException;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.net.Request;
import com.xm6leefun.scan_lib.nfc.bean.ChipResultBean;
import com.xm6leefun.scan_lib.nfc.bean.IdentifyDataResultBean;
import com.xm6leefun.scan_lib.nfc.sdk.NFC;
import com.xm6leefun.scan_lib.nfc.sdk.NFCException;
import com.xm6leefun.scan_lib.nfc.sdk.NTAG21X;
import com.xm6leefun.scan_lib.nfc.sdk.NTAGTech;
import com.xm6leefun.scan_lib.nfc.sdk.UriPrefix;
import com.xm6leefun.scan_lib.nfc.sdk.Util;
import com.xm6leefun.scan_lib.utils.ChipConstant;
import com.xm6leefun.scan_lib.utils.SharePreferenceUtil;
import com.xm6leefun.scan_lib.utils.StrUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/3 14:37
 */
public class NfcModel {

    private IView iView;
    private Gson gson;

    public NfcModel(IView iView) {
        this.iView = iView;
        gson = new Gson();
    }

    private Activity activity;
    /**
     * 处理nfc扫描结果
     * @param activity
     * @param intent
     */
    public void handleNFCIntent(Activity activity, Intent intent) {
        this.activity = activity;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag == null){
            return;
        }
        String[] techList = tag.getTechList();
        final String uuid = Util.bytesToHex(tag.getId());
        for(String v : techList){
            // 新版
            if (v == IsoDep.class.getName()){
                final String url = intent.getDataString();
                if (!StrUtils.isEmpty(url)) {
                    try {
                        //显示加载中
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //开始处理请求
                                initNewPhp(url, uuid, ChipConstant.NTAG_424_DNA_TT);
                            }
                        }, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //开始处理请求
                        initNewPhp(url,uuid,ChipConstant.NTAG_424_DNA_TT);
                    }
                }
                return;
            }
        }
        final String url = readNfcTag(intent);
        if (StrUtils.isEmpty(url)) {
            //跳转到鉴定(处理假芯片)
            dealFakeNfc();
            return;
        }
        String action = intent.getAction().trim();
        if(!action.equals("android.nfc.action.TAG_DISCOVERED") && !action.equals("android.nfc.action.NDEF_DISCOVERED")){
            return;
        }
        String whoHappy="nobody";
        NTAGTech tech = new NTAGTech();
        for(String v : techList){
            if(v == MifareUltralight.class.getName()){
                whoHappy="tt";
                tech.mul = MifareUltralight.get(tag);
            }else if(v == NfcA.class.getName()) {
                whoHappy="a";
                tech.atag = NfcA.get(tag);
            }
        }
        if (whoHappy.equals("tt")) {
            if(tech.mul != null) {
                if (dealTT(tech,url, uuid))
                    return;
            }
        }else if (whoHappy.equals("a")) {
            if (tech.atag != null) {
                onNfcA(uuid, tech.atag,intent);
                return;
            }
        }
    }

    //当获取到NfcA标签
    private void onNfcA(String id, NfcA tag, Intent intent){
        String url="";
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMessage = null;
            if (rawMsgs != null) {
                if (rawMsgs.length > 0) {
                    ndefMessage = (NdefMessage) rawMsgs[0];
                } else {
                    return;
                }
            }
            try {
                NdefRecord ndefRecord = ndefMessage.getRecords()[0];
                Uri uri = parse(ndefRecord);
                url += uri.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StrUtils.isEmpty(url)) {
//                ToastUtil.showToast("链接内容为空，请重新识别");
                return;
            }
            if (StrUtils.isEmpty(id)) {
//                ToastUtil.showToast("id内容为空，请重新识别");
                return;
            }
            //准备请求
            initPhp(url,id,true,ChipConstant.NORMAL, 4,"");
        }
    }

    private boolean dealTT(NTAGTech tech, String url, String uuid) {
        try {
            tech.mul.connect();
            byte[] ver = new byte[0];
            ver = tech.mul.transceive(new byte[]{NFC.GET_VERSION});
            if(ver == null){
                Log.d(NfcModel.class.getName(),"GET_VERSION error");
                return true;
            }
            if(ver.length == 8){
                NTAG21X ntag = null;
                try {
                    ntag = new NTAG21X(uuid,ver,tech);
                    initNtag(ntag, url,uuid);
                } catch (NFCException e) {
                    e.printStackTrace();
                }
            }
            tech.mul.close();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private void initNtag(NTAG21X tag, final String url, String uuid) {
        int breakStatus = 0;  // 芯片是否断开，1是已断开 ，2 是未断开
        try {
            NTAG21X.TTStatus ttStatus = tag.readTTStatus();
            if (ttStatus != null) {
                String openStatus = ttStatus.status();
                if (openStatus.equals("67")) {  // 未被打开
                    breakStatus = 2;  // 芯片是否断开，1是已断开 ，2 是未断开
                } else if (openStatus.equals("79")){  // 已被打开
                    breakStatus = 1;  // 芯片是否断开，1是已断开 ，2 是未断开
                }else if (openStatus.equals("73")){//73未初始化,当作假芯片处理
                    dealFakeNfc();
                    return;
                }else {
                    return;
                }
                Log.i("status", breakStatus + "==mStatus--------------------status----" + openStatus);
                initDialog(url,uuid,ChipConstant.TT, breakStatus,openStatus);
            }
        } catch (IOException e) {
            e.printStackTrace();
            initDialog(url,uuid,ChipConstant.NORMAL, breakStatus,"");
        }
    }

    private void initDialog(final String url, final String uuid,final String isNormal, int breakStatus,String openStatus) {
//        playBeepSoundAndVibrate();
        if (url.startsWith(ApiConfig.URL_HEAD_CHIP) || url.startsWith(ApiConfig.URL_HEAD_CHIP_TRACE)) {
            if (url.contains("nfcCode")) {
                initUrl(url,uuid,true,isNormal, breakStatus,openStatus);
            }else {
                if (url.contains("p=")) {
                    initPhp(url, uuid,true, isNormal, breakStatus,openStatus);
                } else {
                    initUrl(url, uuid,true, isNormal, breakStatus,openStatus);
                }
            }
        } else {
            String linkDoMainName = getDoMainName(url);
            Log.e("host", linkDoMainName);
            //请求验证是否属于我们平台
            identifyLink(linkDoMainName,url,isNormal);
        }
    }

    private void initPhp(String url, String uid, boolean b, final String isNormal, int breakStatus,String openStatus) {
        String [] temp1 = null;
//            判断是否PHP后台
        if (url.startsWith(ApiConfig.URL_HEAD_CHIP) && url.contains("p=")) {
            temp1 = url.split("p=");
            String id = SharePreferenceUtil.getString(ChipConstant.User_ID);
//               请求  鉴定详情接口
            if (!StrUtils.isEmpty(temp1[1])  &&!StrUtils.isEmpty(uid) ) {
                identifyData(id,uid, temp1[1], breakStatus,openStatus,url,isNormal);
            }
        } else if (url.startsWith(ApiConfig.URL_HEAD_CHIP)){  // 新DNA424/wd5502芯片以及其他后续新增芯片，只要有包含本公司域名的即可进入
            initNewPhp(url, uid,ChipConstant.NTAG_WD5502);
        } else {
            String linkDoMainName = getDoMainName(url);
            Log.e("host", linkDoMainName);
            identifyLink(linkDoMainName,url,isNormal);
        }
    }

    private void initUrl( String url ,String uid,boolean isTTcoming,final String isNormal, int breakStatus,String openStatus) {
        try {
            String [] mainUrl = url.split("html/origin.html");

            if (mainUrl==null||mainUrl.length!=2) {
                dealFakeNfc();
                return;
            }
            String canshu =mainUrl[1];
            Log.e("substring",canshu);
            String [] temp1 = null;
//            判断是否PHP后台
            if (canshu.contains("nfcCode")) {
//                Java后台
                temp1 = canshu.split("&");
                if (temp1.length == 3 && temp1[0].split("=").length == 2 && temp1[1].split("=").length == 2 && temp1[2].split("=").length == 2) {
                    if (isTTcoming) {
                        Log.e("substring", "temp1[0]=" + temp1[0] + "temp[1]=" + temp1[1] + "temp[2]=" + temp1[2] + "leg=" + temp1.length);
                        String num = temp1[0].split("=")[1];
                        String goodsId = temp1[1].split("=")[1];
                        String nfcCode = temp1[2].split("=")[1];
//                      发货
                        identifyData(num, goodsId, uid, nfcCode, breakStatus,openStatus,url,isNormal);
                    }
                } else {
                    dealFakeNfc();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    读取nfc中的链接
    private String readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMessage = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                if (rawMsgs.length > 0) {
                    ndefMessage = (NdefMessage) rawMsgs[0];
                    contentSize = ndefMessage.toByteArray().length;
                }
            }
            try {
                NdefRecord ndefRecord = ndefMessage.getRecords()[0];
                final Uri uri = parse(ndefRecord);
                return uri.toString();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 解析NdefRecord中Uri数据
     */
    private Uri parse(NdefRecord record) {
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(record);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }

    /**
     * 处理已知类型的Uri
     */
    private Uri parseWellKnown(NdefRecord ndefRecord) {
        //判断数据是否是Uri类型的
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI))
            return null;
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        String prefix = UriPrefix.URI_PREFIX_MAP.get(payload[0]);
        byte[] prefixBytes = prefix.getBytes(Charset.forName("UTF-8"));
        byte[] fullUri = new byte[prefixBytes.length + payload.length - 1];
        System.arraycopy(prefixBytes, 0, fullUri, 0, prefixBytes.length);
        System.arraycopy(payload, 1, fullUri, prefixBytes.length, payload.length - 1);
        Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
        return uri;
    }

    /**
     * 处理绝对的Uri
     * 没有Uri识别码，也就是没有Uri前缀，存储的全部是字符串
     * @param ndefRecord 描述NDEF信息的一个信息段，一个NdefMessage可能包含一个或者多个NdefRecord
     */
    private Uri parseAbsolute(NdefRecord ndefRecord) {
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
        return uri;
    }

    private void initNewPhp(String url,String nfcUid, final String isNormal){
        // 请求判断是否属于我们平台的链接
        if (url.startsWith(ApiConfig.URL_HEAD_CHIP)) {
            // 走本地鉴定
            HashMap<String, String> mDAndK = getTVD(url);
            if (mDAndK.size() > 0) {
                String t = mDAndK.get("t");
                String v = mDAndK.get("v");
                String d = mDAndK.get("d");
                Log.d("","获取的参数t: " + t + "\n获取的参数v: " + v + "\n获取的参数d: " + d);
                t = StrUtils.isEmpty(t) ? "0" : t;
                v = StrUtils.isEmpty(v) ? "0" : v;
                d = StrUtils.isEmpty(d) ? "0" : d;
                if (!StrUtils.isEmpty(nfcUid)){
                    identifyDataNew(url,isNormal,nfcUid, t, v, d);
                }
            }else{//非本平台芯片逻辑，弹窗提示
                notPlatformTips();
            }
        } else {
            String linkDoMainName = getDoMainName(url);
            identifyLink(linkDoMainName,url,isNormal);
        }
    }

    private String getDoMainName(String url){
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        String scheme = uri.getScheme();//http or https
        String linkDoMainName = scheme + "://" +host;
        Log.e("host", linkDoMainName);
        return linkDoMainName;
    }

    /**
     * 获取芯片链接中的t、v、d
     * @param url
     * @return
     */
    private HashMap<String, String> getTVD(String url){
        HashMap<String, String> hashMap = new HashMap<>();
        String[] split = url.split("\\?");
        if (split.length == 2) {
            String param = split[1];
            try {
                Log.d("","域名：" + split[0].replace("/#/", ""));
                Log.d("","param：" + param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (param.contains("v=") && param.contains("t=") && param.contains("d=")) {  // 含有d= 新版芯片
                try {
                    String[] tvd = param.split("&");
                    for (String aTvd : tvd) {
                        if (aTvd.contains("t=")) {
                            hashMap.put("t", aTvd.replace("t=", ""));
                            Log.d("","参数t：" + aTvd.replace("t=", ""));
                        } else if (aTvd.contains("v=")) {
                            hashMap.put("v", aTvd.replace("v=", ""));
                            Log.d("","参数v：" + aTvd.replace("v=", ""));
                        } else if (aTvd.contains("d=")) {
                            hashMap.put("d", aTvd.replace("d=", ""));
                            Log.d("","参数d：" + aTvd.replace("d=", ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return hashMap;
    }


    //Java重定向PHP接口
    private void identifyData(String num, String goodsId, String nfcUid, String nfcCode, int breakStatus, final String openStatus, final String url, final String isNormal){
        Log.e("NfcModel",ApiConfig.API_identifyData);
        iView.loading(activity.getString(R.string.nfc_jianding_loading));
        new Request(ApiConfig.API_identifyData,ApiConfig.identifyData(num,goodsId,nfcUid,nfcCode,breakStatus)){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("NfcModel",json.toString());
                    final IdentifyDataResultBean identifyDataResultBean = gson.fromJson(json.toString(),IdentifyDataResultBean.class);
                    final int code = identifyDataResultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                iView.getIdentifyDataNewSuccess(identifyDataResultBean.getRecord(),url,isNormal,openStatus);
                            }else{
                                iView.getIdentifyDataFail(identifyDataResultBean.getCode(),identifyDataResultBean.getMsg(),isNormal);
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
                        iView.getIdentifyDataFail(BaseException.NETWORK_ERR_CODE,e.getMessage(),isNormal);
                    }
                });
            }
        };
    }

    // 芯片鉴定详情接口
    private void identifyData(String userId, String nfcUid, String nfcCode, int breakStatus, final String openStatus, final String url, final String isNormal){
        Log.e("NfcModel",ApiConfig.API_identifyData);
        iView.loading(activity.getString(R.string.nfc_jianding_loading));
        new Request(ApiConfig.API_identifyData,ApiConfig.getIdentifyData(userId, nfcUid, nfcCode, breakStatus)){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("NfcModel",json.toString());
                    final IdentifyDataResultBean identifyDataResultBean = gson.fromJson(json.toString(),IdentifyDataResultBean.class);
                    final int code = identifyDataResultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                iView.getIdentifyDataNewSuccess(identifyDataResultBean.getRecord(),url,isNormal,openStatus);
                            }else{
                                iView.getIdentifyDataFail(identifyDataResultBean.getCode(),identifyDataResultBean.getMsg(),isNormal);
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
                        iView.getIdentifyDataFail(BaseException.NETWORK_ERR_CODE,e.getMessage(),isNormal);
                    }
                });
            }
        };
    }

    // 芯片鉴定详情接口  用于新版芯片2.0
    private void identifyDataNew(final String url, final String isNormal, String nfcUid, String t, String v, String d){
        Log.e("NfcModel",ApiConfig.API_identifyData);
        iView.loading(activity.getString(R.string.nfc_jianding_loading));
        new Request(ApiConfig.API_identifyData,ApiConfig.getIdentifyDataNew(nfcUid, t, v, d)){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("NfcModel",json.toString());
                    final IdentifyDataResultBean identifyDataResultBean = gson.fromJson(json.toString(),IdentifyDataResultBean.class);
                    final int code = identifyDataResultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                List<IdentifyDataResultBean.IdentifyDataResultListBean> list = identifyDataResultBean.getRecord();
                                if(list != null && list.size() > 0) {
                                    iView.getIdentifyDataNewSuccess(list, url, isNormal, "");
                                }
                            }else{
                                iView.getIdentifyDataFail(identifyDataResultBean.getCode(),identifyDataResultBean.getMsg(),isNormal);
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
                        iView.getIdentifyDataFail(BaseException.NETWORK_ERR_CODE,e.getMessage(),isNormal);
                    }
                });
            }
        };
    }

    // 三方芯片查询接口
    private void identifyLink(String chipLink, final String url, final String isNormal){
        Log.e("NfcModel",ApiConfig.API_identifyLink);
        iView.loading(activity.getString(R.string.nfc_jianding_loading));
        new Request(ApiConfig.API_identifyLink,ApiConfig.identifyLink(chipLink)){
            @Override
            public void success(StringBuffer json) {
                try {
                    Log.e("NfcModel",json.toString());
                    final ChipResultBean chipResultBean = gson.fromJson(json.toString(),ChipResultBean.class);
                    final int code = chipResultBean.getCode();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iView.dismiss();
                            if (200 == code) {
                                iView.getIdentifyLinkSuccess(chipResultBean.getRecord(),url,isNormal);
                            }else{
                                iView.getIdentifyLinkFail(chipResultBean.code,chipResultBean.getMsg());
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
                        iView.getIdentifyLinkFail(BaseException.NETWORK_ERR_CODE,e.getMessage());
                    }
                });
            }
        };
    }

    // 处理假芯片
    private void dealFakeNfc(){
        Log.e("NfcModel","处理假芯片");
        iView.dealFakeNfc();
    }

    // 非本平台芯片，请谨防假冒弹窗
    private void notPlatformTips(){
        Log.e("NfcModel","非本平台芯片，请谨防假冒弹窗");
        iView.notPlatformTips();
    }

    interface IView{
        void loading(String s);
        void dismiss();
        void getIdentifyLinkSuccess(ChipResultBean.Entity entity, String url, String isNormal);
        void getIdentifyLinkFail(int code,String errorMessage);
        //假芯片回调
        void dealFakeNfc();
        void getIdentifyDataFail(int code,String msg,String isNormal);
        // 芯片鉴定详情接口  用于新版芯片2.0
        void getIdentifyDataNewSuccess(List<IdentifyDataResultBean.IdentifyDataResultListBean> identifyDataResultListBeans, String url, String isNormal, String openStatus);
        //非本平台芯片，请谨防假冒
        void notPlatformTips();
    }

}
