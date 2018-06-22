package example.com.seereal;

import android.widget.ImageView;

public class FriendData {
    private String name;
    private ImageView img;
   // private String age;

    public FriendData(String name)//, String age) {
    {
        this.name = name;
     //   this.age = age;
    }
    public FriendData(String name, ImageView img)//, String age) {
    {
        this.name = name;
        this.img = img;
        //   this.age = age;
    }

    public String getName() {
        return name;
    }

  //  public String getAge() {  return age;   }


    public ImageView getImg() {
        return img;
    }
}
