package com.xm6leefun.scan_lib.nfc.bean;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2020/11/24 16:17
 */
public class ChipResultBean {
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
        private String companyName;
        private boolean disabled;
        private String chipLink;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public String getChipLink() {
            return chipLink;
        }

        public void setChipLink(String chipLink) {
            this.chipLink = chipLink;
        }
    }
}
