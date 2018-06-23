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
import android.widget.FrameLayout;
import android.widget.ImageView;


public class MoreFragment extends Fragment {
    boolean inList =true;
    ImageView myList,favoriteList;
     Fragment contents;
     Fragment favoriteContents;
     FrameLayout frameLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,null);
        contents =  new MyList();
        favoriteContents = new FavoriteList();

        myList =(ImageView)view.findViewById(R.id.list_image);
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inList) {
                    myList.setImageResource(R.drawable.selected_list);
                    int color = ContextCompat.getColor(getActivity(),R.color.black);
                    favoriteList.setColorFilter(color);
                   onFragmentChanged(0);
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
                    onFragmentChanged(1);
                    inList=false;

                }
            }
        });


        return view;
    }

    public void onFragmentChanged(int index){
        if(index==0){
            getChildFragmentManager().beginTransaction().replace(R.id.contents_fragment,contents).commit();
        } else if(index ==1 ){
            getChildFragmentManager().beginTransaction().replace(R.id.contents_fragment,favoriteContents).commit();
        }
    }

}