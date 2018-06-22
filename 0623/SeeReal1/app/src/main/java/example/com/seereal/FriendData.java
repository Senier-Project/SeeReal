package example.com.seereal;

public class FriendData {
    private String name;
    private int img;
   // private String age;

    public FriendData(String name)//, String age) {
    {
        this.name = name;
     //   this.age = age;
    }
    public FriendData(String name, int img)//, String age) {
    {
        this.name = name;
        this.img = img;
        //   this.age = age;
    }

    public String getName() {
        return name;
    }

  //  public String getAge() {  return age;   }


    public int getImg() {
        return img;
    }
    public void setImage(int img) {
        this.img = img;
    }
}
