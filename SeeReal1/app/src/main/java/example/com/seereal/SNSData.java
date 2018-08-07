package example.com.seereal;

public class SNSData {
    private int img;
    private String title;
    private String tag;

    public SNSData(int img, String title){
        this.img = img;
        this.title = title;
    }
    public SNSData(int img, String title, String tag){
        this.img = img;
        this.title = title;
        this.tag = tag;
    }

    public int getImg() {  return img;   }
    public String getTitle() {  return title;   }
    public String getTag() {  return tag;   }

}
