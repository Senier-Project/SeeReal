package example.com.seereal;

import java.util.HashMap;

public class ProfileImgInfo {
    //private HashMap<String, Integer> in;
    //private HashMap<String, Integer> ex;

    private HashMap<String, Integer> img;

    public ProfileImgInfo() {
    }

    public ProfileImgInfo(HashMap<String, Integer> img) {
        this.img = img;
    }

    public void setCate(HashMap<String, Integer> img) {
        this.img = img;
    }

    public HashMap<String, Integer> getProfileImg() {
        return this.img;
    }
}