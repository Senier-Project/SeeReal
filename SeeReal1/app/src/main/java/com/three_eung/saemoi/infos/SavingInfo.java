package com.three_eung.saemoi.infos;

import java.util.ArrayList;

public class SavingInfo implements Cloneable {
    private String id;
    private String title;
    private int value;
    private int count;
    private String startDate;
    private ArrayList<String> savedDate;

    public SavingInfo() {}

    public SavingInfo(String title, int value, int count, String startDate) {
        this.title = title;
        this.value = value;
        this.count = count;
        this.startDate = startDate;
        this.savedDate = new ArrayList<>();
    }

    public SavingInfo(String title, int value, int count, String startDate, ArrayList<String> savedDate) {
        this.title = title;
        this.value = value;
        this.count = count;
        this.startDate = startDate;
        this.savedDate = savedDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSavedDate(ArrayList<String> savedDate) {
        this.savedDate = savedDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getSavedDate() {
        return savedDate;
    }

    @Override
    protected Object clone() {
        try {
            SavingInfo savingInfo = (SavingInfo)super.clone();
            return savingInfo;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
