package com.three_eung.saemoi.infos;

import java.util.HashMap;

public class CategoryInfo {
    private HashMap<String, Integer> in;
    private HashMap<String, Integer> ex;

    public CategoryInfo() {}

    public CategoryInfo(HashMap<String, Integer> in, HashMap<String, Integer> ex) {
        this.in = in;
        this.ex = ex;
    }

    public void setEx(HashMap<String, Integer> ex) {
        this.ex = ex;
    }

    public void setIn(HashMap<String, Integer> in) {
        this.in = in;
    }

    public HashMap<String, Integer> getEx() {
        return ex;
    }

    public HashMap<String, Integer> getIn() {
        return in;
    }
}
