package com.xm6leefun.scan_lib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xm6leefun.scan_lib.net.ApiConfig;

/**
 * 缓存sp工具类
 */
public class SharePreferenceUtil {
    private static final String FILE_NAME = "zwd_sdk_config";
    private static SharedPreferences sp = ApiConfig.getApplication().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

    public SharePreferenceUtil() {
    }

    public static void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public static void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static void setFloat(String key, Float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public static Float getFloat(String key) {
        return sp.getFloat(key, 0.0F);
    }

    public static void setLong(String key, Long value) {
        sp.edit().putLong(key, value).apply();
    }

    public static Long getLong(String key) {
        return sp.getLong(key, 0L);
    }

    public static void removeByKey(String key) {
        sp.edit().remove(key).apply();
    }

    public static void removeAll() {
        sp.edit().clear().apply();
    }
}
