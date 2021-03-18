package com.xm6leefun.scan_lib.appraisa.bean;


/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/12/25 14:07
 */
public class ChipDrawResultBean {

    public int code;
    public String msg;
    public Entity record;

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

    public Entity getRecord() {
        return record;
    }

    public void setRecord(Entity record) {
        this.record = record;
    }

    public static class Entity{
        private int integralStatus;//积分领取状态：1获得积分未绑定；2没有获得积分；3领取成功；4已领取；5，登录领取积分
        private long num;

        public int getIntegralStatus() {
            return integralStatus;
        }

        public void setIntegralStatus(int integralStatus) {
            this.integralStatus = integralStatus;
        }

        public long getNum() {
            return num;
        }

        public void setNum(long num) {
            this.num = num;
        }
    }
}
