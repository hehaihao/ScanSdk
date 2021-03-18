package com.xm6leefun.scan_lib.base;


/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/12/25 14:07
 */
public class ResultBean {

    public int code;
    public String msg;
    public Object record;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getRecord() {
        return record;
    }

    public void setRecord(Object record) {
        this.record = record;
    }

}
