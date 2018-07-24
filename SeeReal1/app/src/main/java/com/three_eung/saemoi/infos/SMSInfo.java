package com.three_eung.saemoi.infos;

public class SMSInfo {
    private String msg;
    private String value;
    private String date;

    public SMSInfo() {}
    public SMSInfo(String msg, String value, String date) {
        this.msg = msg;
        this.value = value;
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }
}
