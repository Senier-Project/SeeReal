package example.com.seereal;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class InitApp extends Application{

    private static InitApp self;
    public volatile static FirebaseAuth sAuth;
    public volatile static FirebaseUser sUser;
    public static FirebaseDatabase sDatabase;
    private DatabaseReference mProfileImgRef;
    private ChildEventListener mProfileImgListener;
    private CategoryInfo defaultCate = null;
    private HashMap<String, Integer> mCate;
    private int profileImg;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
        sAuth = FirebaseAuth.getInstance();
        sUser = sAuth.getCurrentUser();
    }

   public void initDatabase() {
        sDatabase = FirebaseDatabase.getInstance();

       mProfileImgRef = this.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("img");


       this.sDatabase.getReference().child("category").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               defaultCate = dataSnapshot.getValue(CategoryInfo.class);
               updateCate();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       });

       mProfileImgListener = new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               profileImg = (int) (long) dataSnapshot.getValue();

               //updateData();
           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {
               profileImg = (int) (long) dataSnapshot.getValue();
               Log.d("susu","initAPP이에오"+profileImg);
               //updateData();
           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {
               profileImg = 0;
               //updateData();
           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       };

       mProfileImgRef.addChildEventListener(mProfileImgListener);
    }


    private void updateCate() {
        mCate.clear();

        if (defaultCate != null) {
            if (defaultCate.getCate() != null) {
                HashMap<String, Integer> tmpList = defaultCate.getCate();
                for (String tmp : tmpList.keySet()) {
                    mCate.put(tmp.toString(), tmpList.get(tmp));
                }
            }


        }

    }

    public HashMap<String, Integer> getCate() {
        return (HashMap<String, Integer>) mCate.clone();
    }
    public int getProfileImg(){
        return profileImg;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}


