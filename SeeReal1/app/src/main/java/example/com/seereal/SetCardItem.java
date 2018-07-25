package example.com.seereal;

import java.util.ArrayList;

public class SetCardItem {

    private String title;
    private String tag;
    private int image;
    private int isLike;
    private CardViewItem item = new CardViewItem();


    public SetCardItem(String title, String tag, int image, int isLike) {

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
