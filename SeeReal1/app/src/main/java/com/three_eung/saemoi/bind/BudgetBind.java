package com.three_eung.saemoi.bind;

public class BudgetBind {
    private final String title;
    private final String value;

    public BudgetBind(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
