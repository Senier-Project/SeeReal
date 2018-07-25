package com.three_eung.saemoi.bind;

import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.infos.DayInfo;

public class CalendarBind {
    private final String day;
    private final String in;
    private final String ex;

    public CalendarBind(DayInfo dayInfo) {
        if (dayInfo.getDay() == 0) {
            this.day = "";
        } else {
            this.day = String.valueOf(dayInfo.getDay());
        }
        this.in = "+" + Utils.toCurrencyFormat(dayInfo.getIncome());
        this.ex = "-" + Utils.toCurrencyFormat(dayInfo.getExpend());
    }

    public String getDay() {
        return day;
    }

    public String getIn() {
        return in;
    }

    public String getEx() {
        return ex;
    }
}
