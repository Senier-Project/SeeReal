package com.three_eung.saemoi.infos;

/**
 * Created by CH on 2018-02-18.
 */

public class DayInfo {
    private int day, income, expend;

    public DayInfo() {
        this.day = 0;
        this.income = 0;
        this.expend = 0;
    }

    public DayInfo(int day) {
        this.day = day;
        this.income = 0;
        this.expend = 0;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setExpend(int expend) {
        this.expend = expend;
    }

    public int getDay() {
        return day;
    }

    public int getIncome() {
        return income;
    }

    public int getExpend() {
        return expend;
    }
}
