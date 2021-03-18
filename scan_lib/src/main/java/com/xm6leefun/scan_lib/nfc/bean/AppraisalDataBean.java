package com.xm6leefun.scan_lib.nfc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/12/25 13:33
 */
public class AppraisalDataBean implements Parcelable {
    private String url;
    private String isTrueStatus;
    private String openStatus;
    private String breakStatus; // 芯片状态：1：断开  2：未断开
    private String istt;
    private int activateState;  // 积分活动状态 ：1，活动未开始 ; 2活动进行中 ；3，活动已结束；4，活动已关闭
    private boolean isActivity;  // 积分活动是否存在
    private boolean integralStatus;  // 积分是否被领取 ，true：已被领取；false: 未被领取
    private String goodsName;
    private String goodsLogo;

    public AppraisalDataBean() {
    }

    protected AppraisalDataBean(Parcel in) {
        url = in.readString();
        isTrueStatus = in.readString();
        openStatus = in.readString();
        breakStatus = in.readString();
        istt = in.readString();
        activateState = in.readInt();
        isActivity = in.readByte() != 0;
        integralStatus = in.readByte() != 0;
        goodsName = in.readString();
        goodsLogo = in.readString();
    }

    public static final Creator<AppraisalDataBean> CREATOR = new Creator<AppraisalDataBean>() {
        @Override
        public AppraisalDataBean createFromParcel(Parcel in) {
            return new AppraisalDataBean(in);
        }

        @Override
        public AppraisalDataBean[] newArray(int size) {
            return new AppraisalDataBean[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsTrueStatus() {
        return isTrueStatus;
    }

    public void setIsTrueStatus(String isTrueStatus) {
        this.isTrueStatus = isTrueStatus;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public String getBreakStatus() {
        return breakStatus;
    }

    public void setBreakStatus(String breakStatus) {
        this.breakStatus = breakStatus;
    }

    public String getIstt() {
        return istt;
    }

    public void setIstt(String istt) {
        this.istt = istt;
    }

    public int getActivateState() {
        return activateState;
    }

    public void setActivateState(int activateState) {
        this.activateState = activateState;
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

    public boolean isActivity() {
        return isActivity;
    }

    public void setIsActivity(boolean isActivity) {
        this.isActivity = isActivity;
    }

    public boolean isIntegralStatus() {
        return integralStatus;
    }

    public void setIntegralStatus(boolean integralStatus) {
        this.integralStatus = integralStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(url);
        dest.writeString(isTrueStatus);
        dest.writeString(openStatus);
        dest.writeString(breakStatus);
        dest.writeString(istt);
        dest.writeInt(activateState);
        dest.writeByte((byte) (isActivity ? 1 : 0));
        dest.writeByte((byte) (integralStatus ? 1 : 0));
        dest.writeString(goodsName);
        dest.writeString(goodsLogo);
    }

}
