package com.three_eung.saemoi.infos;

public class HousekeepInfo implements Cloneable {
    private String id;
    private int value;
    private boolean isIncome;
    private String category;
    private boolean isBudget;
    private String memo;
    private String date;

    public HousekeepInfo() {
        this.value = 0;
        this.isIncome = true;
        this.category = null;
        this.isBudget = false;
        this.memo = null;
        this.date = null;
    }

    public HousekeepInfo(String category, int value, boolean isIncome, boolean isBudget, String memo, String date) {
        this.category = category;
        this.value = value;
        this.isIncome = isIncome;
        this.isBudget = isBudget;
        this.memo = memo;
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBudget(boolean budget) {
        isBudget = budget;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDate(String date) { this.date = date; }

    public int getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }

    public String getMemo() {
        return memo;
    }

    public String getDate() { return date; }

    public boolean getIsBudget() {
        return isBudget;
    }

    public boolean getIsIncome() {
        return isIncome;
    }

    public String getId() {
        return id;
    }

    @Override
    protected Object clone() {
        try {
            HousekeepInfo housekeepInfo = (HousekeepInfo)super.clone();
            return housekeepInfo;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
