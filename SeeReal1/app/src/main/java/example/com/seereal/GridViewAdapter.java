package example.com.seereal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    Context c;
    ArrayList<CardViewItem> item;

    GridViewAdapter(Context c, ArrayList<CardViewItem> item){
        this.c= c;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView= LayoutInflater.from(c).inflate(R.layout.card_view,parent,false);
        }
        final CardViewItem item = (CardViewItem) this.getItem(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView titleView = convertView.findViewById(R.id.titleText);
        TextView tagView = convertView.findViewById(R.id.tagText);

        imageView.setImageResource(item.getImage());
        titleView.setText(item.getTitle());
        tagView.setText(item.getTag());

        return convertView;
    }
}
