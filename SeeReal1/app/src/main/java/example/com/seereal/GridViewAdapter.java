package example.com.seereal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<CardViewItem> item;
    ArrayList<CardViewItem> filteredItemList;
    Filter listFilter;
    private int width;
    private int height;

    GridViewAdapter(Context c, ArrayList<CardViewItem> item, int width, int height) {
        this.c = c;
        this.item = item;
        this.width = width;
        this.height = height;
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

        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.card_view, parent, false);
        }
        final CardViewItem item = (CardViewItem) this.getItem(position);

        final ImageView imageView = convertView.findViewById(R.id.imageView);
        final ImageView isLike = convertView.findViewById(R.id.isLike);
        TextView titleView = convertView.findViewById(R.id.titleText);
        TextView tagView = convertView.findViewById(R.id.tagText);

        imageView.getLayoutParams().height = height / 5;
        imageView.getLayoutParams().width = width / 3;
        imageView.requestLayout();

        Log.d("susu", "55  "+item.getImage());

      //  if(item.getImage() != null)
        //getResources(), R.drawable.test02);
        Bitmap bitmapImage;
        while(true){
            bitmapImage = BitmapFactory.decodeFile(item.getImage());
            if(bitmapImage != null) break;
        }
        //Bitmap bitmapImage = BitmapFactory.decodeFile(item.getImage());
        // testStorage.setImageBitmap(bitmapImage);

        imageView.setImageBitmap(bitmapImage);
        //imageView.setImageResource(R.drawable.car_battery);//.setImageBitmap(bitmapImage);
        isLike.setImageResource(item.getIsLike());
        titleView.setText(item.getTitle());
        tagView.setText(item.getTag());

        isLike.setOnClickListener(new View.OnClickListener() {

            int i = 0;

            @Override
            public void onClick(View v) {
                i = 1 - i;
                if (i == 0) {
                    Log.d("susu", "6  ");
                    isLike.setImageResource(R.drawable.baseline_favorite_border_black_18dp);
                } else {
                    isLike.setImageResource(R.drawable.baseline_favorite_black_18dp);
                }
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = item;
                results.count = item.size();
            } else {
                ArrayList<CardViewItem> itemList = new ArrayList<>();

                for (CardViewItem item : item) {
                    if (item.getTitle().contains(constraint.toString())) {

                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItemList = (ArrayList<CardViewItem>) results.values;
            // notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
