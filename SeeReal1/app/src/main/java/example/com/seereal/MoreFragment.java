package example.com.seereal;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MoreFragment extends Fragment {
    boolean inList =true;
    ImageView myList,favoriteList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,null);
        final Fragment contents =  new MyList();
        final Fragment favoriteContents = new FavoriteList();

        myList =(ImageView)view.findViewById(R.id.list_image);
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inList) {
                    myList.setImageResource(R.drawable.selected_list);
                    int color = ContextCompat.getColor(getActivity(),R.color.black);
                    favoriteList.setColorFilter(color);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.contents_fragment,contents ).commit();
                    inList=true;
                }
            }
        });
        favoriteList=(ImageView)view.findViewById(R.id.star_image);
        favoriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inList) {
                    myList.setImageResource(R.drawable.list);
                    int color = ContextCompat.getColor(getActivity(),R.color.colorPrimary);
                    favoriteList.setColorFilter(color);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.contents_fragment,favoriteContents ).commit();
                    inList=false;

                }
            }
        });


        return view;
    }

}