package example.com.seereal;

public class SetCardItem {

    private String title;
    private String tag;
    private String image;
    private int isLike;
    private CardViewItem item = new CardViewItem();


    public SetCardItem(String title, String tag, String image, int isLike) {

        this.title = title;
        this.tag = tag;
        this.image = image;
        this.isLike = isLike;
    }

    public CardViewItem getList()
    {
        item.setTitle(title);
        item.setTag(tag);
        item.setImage(image);
        item.setIsLike(isLike);
        return item;
    }
}
