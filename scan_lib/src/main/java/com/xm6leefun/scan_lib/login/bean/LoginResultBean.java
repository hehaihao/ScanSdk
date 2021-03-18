package com.xm6leefun.scan_lib.login.bean;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/11/25 14:31
 */
public class LoginResultBean {
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
        private UserInfoBean userInfo;

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public static class UserInfoBean {

            private String coinSum;
            private String coinSumStr;
            private String id;
            private String headPortrait;
            private String mobile;
            private String token;
            private String nickName;
            private String wxName;
            private String userId;
            private String userUuid;
            private String openId;
            private int isSetPwd;

            public String getWxName() {
                return wxName;
            }

            public void setWxName(String wxName) {
                this.wxName = wxName;
            }

            public String getCoinSum() {
                return coinSum;
            }

            public void setCoinSum(String coinSum) {
                this.coinSum = coinSum;
            }

            public String getCoinSumStr() {
                return coinSumStr;
            }

            public void setCoinSumStr(String coinSumStr) {
                this.coinSumStr = coinSumStr;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHeadPortrait() {
                return headPortrait;
            }

            public void setHeadPortrait(String headPortrait) {
                this.headPortrait = headPortrait;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserUuid() {
                return userUuid;
            }

            public void setUserUuid(String userUuid) {
                this.userUuid = userUuid;
            }

            public String getOpenId() {
                return openId;
            }

            public void setOpenId(String openId) {
                this.openId = openId;
            }

            public int getIsSetPwd() {
                return isSetPwd;
            }

            public void setIsSetPwd(int isSetPwd) {
                this.isSetPwd = isSetPwd;
            }

            @Override
            public String toString() {
                return "UserInfoBean{" +
                        "coinSum='" + coinSum + '\'' +
                        ", coinSumStr='" + coinSumStr + '\'' +
                        ", id='" + id + '\'' +
                        ", headPortrait='" + headPortrait + '\'' +
                        ", mobile='" + mobile + '\'' +
                        ", token='" + token + '\'' +
                        ", nickName='" + nickName + '\'' +
                        ", userId='" + userId + '\'' +
                        ", userUuid='" + userUuid + '\'' +
                        ", openId='" + openId + '\'' +
                        ", isSetPwd=" + isSetPwd +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "LoginResultBean{" +
                    userInfo.toString() +
                    '}';
        }
    }
}
