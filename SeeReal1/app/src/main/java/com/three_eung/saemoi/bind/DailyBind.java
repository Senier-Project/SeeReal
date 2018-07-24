package com.three_eung.saemoi.bind;

import android.view.View;

public class DailyBind {
    private final String inex;
    private final String value;
    private final String memo;
    private final String cate;
    private final View.OnClickListener deleteListener;

    public DailyBind(String inex, String value, String memo, String cate, View.OnClickListener deleteListener) {
        this.inex = inex;
        this.value = value;
        this.memo = memo;
        this.cate = cate;
        this.deleteListener = deleteListener;
    }

    public String getValue() {
        return value;
    }

    public String getMemo() {
        return memo;
    }

    public View.OnClickListener getDeleteListener() {
        return deleteListener;
    }

    public String getCate() {
        return cate;
    }

    public String getInex() {
        return inex;
    }
}
