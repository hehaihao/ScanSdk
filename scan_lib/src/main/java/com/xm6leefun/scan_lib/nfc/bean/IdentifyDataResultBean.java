package com.xm6leefun.scan_lib.nfc.bean;

import java.util.List;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/12/21 14:13
 */
public class IdentifyDataResultBean {
    public int code;
    public String msg;
    public List<IdentifyDataResultListBean> record;

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

    public List<IdentifyDataResultListBean> getRecord() {
        return record;
    }

    public void setRecord(List<IdentifyDataResultListBean> record) {
        this.record = record;
    }

    public static class IdentifyDataResultListBean{
        private boolean status;
        private boolean isActivity;
        private int activateState;
        private boolean integralStatus;
        private String goodsName;
        private String goodsLogo;
        private String countMoney;
        private String countUse;
        private String countUseMoney;
        private String countsize;
        private String createTimeStr;
        private int flag;
        private String high;
        private String name;
        private String num;
        private String pic;
        private String platformName;
        private String size;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public boolean isIsActivity() {
            return isActivity;
        }

        public void setIsActivity(boolean isActivity) {
            this.isActivity = isActivity;
        }

        public int getActivateState() {
            return activateState;
        }

        public void setActivateState(int activateState) {
            this.activateState = activateState;
        }

        public boolean isIntegralStatus() {
            return integralStatus;
        }

        public void setIntegralStatus(boolean integralStatus) {
            this.integralStatus = integralStatus;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsLogo() {
            return goodsLogo;
        }

        public void setGoodsLogo(String goodsLogo) {
            this.goodsLogo = goodsLogo;
        }

        public String getCountMoney() {
            return countMoney;
        }

        public void setCountMoney(String countMoney) {
            this.countMoney = countMoney;
        }

        public String getCountUse() {
            return countUse;
        }

        public void setCountUse(String countUse) {
            this.countUse = countUse;
        }

        public String getCountUseMoney() {
            return countUseMoney;
        }

        public void setCountUseMoney(String countUseMoney) {
            this.countUseMoney = countUseMoney;
        }

        public String getCountsize() {
            return countsize;
        }

        public void setCountsize(String countsize) {
            this.countsize = countsize;
        }

        public String getCreateTimeStr() {
            return createTimeStr;
        }

        public void setCreateTimeStr(String createTimeStr) {
            this.createTimeStr = createTimeStr;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getPlatformName() {
            return platformName;
        }

        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}
