package com.three_eung.saemoi.infos;

public class BudgetInfo {
    private String category;
    private int value;

    public BudgetInfo() {}
    public BudgetInfo(String category) {
        this.category = category;
        this.value = 0;
    }
    public BudgetInfo(String category, int value) {
        this.category = category;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }
}
