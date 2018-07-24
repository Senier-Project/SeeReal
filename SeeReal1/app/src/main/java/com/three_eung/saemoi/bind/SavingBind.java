package com.three_eung.saemoi.bind;

import android.view.View;

public class SavingBind {
    private final String title;
    private final String unit;
    private final String total;
    private final View.OnClickListener listener;

    public SavingBind(String title, String unit, String total, View.OnClickListener listener) {
        this.title = title;
        this.unit = unit;
        this.total = total;
        this.listener = listener;
    }

    public String getTitle() {
        return title;
    }

    public String getTotal() {
        return total;
    }

    public String getUnit() {
        return unit;
    }

    public View.OnClickListener getListener() {
        return listener;
    }
}
