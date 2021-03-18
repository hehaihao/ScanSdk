package com.xm6leefun.scan_lib.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 项目：BlockUser
 * 文件描述：处理链接数据工具类
 * 作者：ljj
 * 创建时间：2020/6/16
 */
public class DealUrlDataUtils {

    /**
     * 获取芯片链接中的d和k
     * @param url
     * @return
     */
    public static HashMap<String, String> getDAndK(String url){
        HashMap<String, String> hashMap = new HashMap<>();
        String[] split = url.split("/#/\\?");
        if (split.length == 2) {
            String param = split[1];
            if (param.contains("d=")) {  // 含有d= 新版芯片
                if (param.contains("k=")) {
                    // k=  参数k
                    String substringK = param.substring(param.indexOf("k=") + 2, param.indexOf("&"));
                    hashMap.put("k", substringK);
                }
                // d=  参数d
                String substringD = param.substring(param.indexOf("d=") + 2);
                hashMap.put("d", substringD);
                // 截取是否打开芯片的判断位
                String substringIsOpened = substringD.substring(32, 34);
                hashMap.put("IsOpened", substringIsOpened);
//                if ("CC".equals(substringIsOpened)) {
//                    LogUtil.d("芯片未打开");
//                } else if ("OO".equals(substringIsOpened)) {
//                    LogUtil.d("芯片已被破坏");
//                }
            }
        }
        return hashMap;
    }
    /**
     * 获取芯片链接中的t、v、d
     * @param url
     * @return
     */
    public static HashMap<String, String> getTVD(String url){
        HashMap<String, String> hashMap = new HashMap<>();
        String[] split = url.split("\\?");
        if (split.length == 2) {
            String param = split[1];
            try {
//                LogUtil.d("域名：" + split[0].replace("/#/", ""));
//                LogUtil.d("param：" + param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (param.contains("v=") && param.contains("t=") && param.contains("d=")) {  // 含有d= 新版芯片
                try {
                    String[] tvd = param.split("&");
//                    LogUtil.d(Arrays.toString(tvd) + "==参数tvd：" + tvd.length);
                    for (String aTvd : tvd) {
                        if (aTvd.contains("t=")) {
                            hashMap.put("t", aTvd.replace("t=", ""));
                        } else if (aTvd.contains("v=")) {
                            hashMap.put("v", aTvd.replace("v=", ""));
                        } else if (aTvd.contains("d=")) {
                            hashMap.put("d", aTvd.replace("d=", ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return hashMap;
    }

    /**
     * 判断是否存在本公司的标志位
     * 若不包含则返回空字符串
     * @param url
     * @return
     */
    public static String isBelong2Us (String url){
        List<String> list = Arrays.asList("p=", "w=", "n=", "nfcCode=");
        for (String s : list){
            if (url.contains(s)){
                return s;
            }
        }
        return "";
    }
}
