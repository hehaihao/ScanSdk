package com.xm6leefun.scan_lib.exception;

import java.io.IOException;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/3/4 15:29
 */
public class BaseException extends IOException {
    /**
     * 解析数据失败
     */
    public static final String PARSE_ERROR_MSG = "解析数据失败";
    /**
     * 网络问题
     */
    public static final String BAD_NETWORK_MSG = "网络问题";
    /**
     * 连接错误
     */
    public static final String CONNECT_ERROR_MSG = "连接错误";
    /**
     * 连接超时
     */
    public static final String CONNECT_TIMEOUT_MSG = "连接超时";
    public static final int NETWORK_ERR_CODE = -9999;
    public static final int PARSE_ERR_CODE = -8888;
    public static final int RE_LOGIN = 1007;//token过期，重新登录

    private String errorMsg;
    private int errorCode;

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    BaseException(int errorCode,String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public BaseException(int errorCode, String message) {
        this.errorMsg = message;
        this.errorCode = errorCode;
    }
}
