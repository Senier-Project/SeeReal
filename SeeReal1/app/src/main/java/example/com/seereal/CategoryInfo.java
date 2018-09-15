package example.com.seereal;

import java.util.HashMap;

public class CategoryInfo {
 //   private HashMap<String, Integer> in;
  //  private HashMap<String, Integer> ex;
 private HashMap<String, Integer> cate;

    public CategoryInfo() {}

    public CategoryInfo(HashMap<String, Integer> cate) {
        this.cate = cate;
    }

    public void setCate(HashMap<String, Integer> cate) {
        this.cate = cate;
    }

    public HashMap<String, Integer> getCate() {
        return cate;
    }

}
