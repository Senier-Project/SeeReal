package example.com.seereal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreFragment extends Fragment {
    boolean inList =true;
    ImageView profileImg;
    ImageView myList,favoriteList;
     Fragment contents;
     Fragment favoriteContents;
     int selectF=9;

     private FirebaseAuth mAuth;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       //suh추가
       // InitApp.sUser.getDisplayName()

        View view = inflater.inflate(R.layout.fragment_more,null);

        TextView userName = (TextView) view.findViewById(R.id.user_id);
        TextView userEmail = (TextView) view.findViewById(R.id.user_email);
        profileImg = view.findViewById(R.id.image_profile);

        userName.setText(InitApp.sUser.getDisplayName());
        userEmail.setText(InitApp.sUser.getEmail());


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = InitApp.sUser.getUid();
        DatabaseReference mData = mDatabase.child("users").child(uid).child("img");


        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int selected=0;
                //
                if(dataSnapshot.getValue(Integer.class) == null){
                    selected = 0;

                }
                selected = dataSnapshot.getValue(Integer.class);

                //selectF = selected;
                profileImg.setImageResource(Utils.getProfileImgDrawable(selected));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w( "Failed to read value.", databaseError.toException());
            }
        });


        //int selected = 5;

        //원래
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