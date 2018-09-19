package example.com.seereal;

public class FriendData {
    private String name;
    private  String email;
    private int img;
   // private String age;

    public FriendData(String name)//, String age) {
    {
        this.name = name;
     //   this.age = age;
    }
    public FriendData(String name, String email) {

        this.name = name;
        this.email = email;
        //   this.age = age;
    }
    public FriendData(String name, String email, int img) {

        this.img = img;
        this.name = name;
        this.email = email;
        //   this.age = age;
    }
    public String getName() {
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public void setName (String name){
        this.name = name;
    }
    public void setEmail (String email){
        this.email = email;
    }
    public int getImg() { return img; }
    public void setImage(int img) {  this.img = img; }





}
