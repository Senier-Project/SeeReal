package com.three_eung.saemoi;

import java.util.HashMap;

public class CardItem {
    private String title;
    private HashMap<String, String> itemList;

    public CardItem() {
        this.title = null;
        this.itemList = new HashMap<>();
    }

    public CardItem(String title) {
        this.title = title;
        this.itemList = new HashMap<>();
    }

    public CardItem(int flag) {
        initItem(flag);
    }

    public CardItem(String title, HashMap<String, String> itemList) {
        this.title = title;
        this.itemList = itemList;
    }

    private void initItem(int flag) {
        switch (flag) {
            case 0:
                this.title = "가계부";
                this.itemList = new HashMap<>();
                this.itemList.put("first_key", "오늘 지출 가능 금액");
                this.itemList.put("second_key", "오늘 수입");
                this.itemList.put("third_key", "오늘 지출");
                break;
            case 1:
                this.title = "아끼기";
                this.itemList = new HashMap<>();
                this.itemList.put("first_key", "총 아낀 돈");
                this.itemList.put("second_key", "오늘 아낀 돈");
                this.itemList.put("third_key", "오늘 아낀 횟수");
                break;
        }
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, String> getItemList() {
        return itemList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setItemList(HashMap<String, String> itemList) {
        this.itemList = itemList;
    }
}
