package example.com.seereal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendFragment extends Fragment {
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    private RecyclerView mRecyclerView;
    private View mView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<FriendData> mData;

   private int mProfileImg;


    private UserModel destinationUserModel ;
    private String requestContext;

    private Context mContext;


    public static FriendFragment newInstance() {
        FriendFragment friendFragment = new FriendFragment();
        return friendFragment;
    }

    public FriendFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.friendListVIew);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
       // https:www.youtube.com/watch?v=MGOrkrLpWgYuse
        mRecyclerView.setLayoutManager(mLayoutManager);
        mContext=getContext();

        //mData = new ArrayList<>();
        //ImageView  myImg = null;
        //myImg.setImageDrawable(getResources().getDrawable(R.drawable.but_call2));

        //susu 여기 지움
/*
        FriendData friendData = new FriendData("재현");
        friendData.setImage(R.drawable.imgeje);
        //  FriendData friendData = new FriendData("수화", "23");
        mData.add(friendData);
        //추가한거

        friendData = new FriendData("수화");
        friendData.setImage(R.drawable.imagesu);
        mData.add(friendData);
*/
        //mAdapter = new FriendFragmentAdapter(mData);
        mRecyclerView.setAdapter(new FriendFragmentAdapter());

        return mView;
    }

    class FriendFragmentAdapter extends RecyclerView.Adapter<FriendFragmentAdapter.ViewHolder> {
        private ArrayList<FriendData> items;
        final String packageName = "com.PleaseCompany.please";
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);

        public FriendFragmentAdapter() {
            items = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    items.clear();

                    FriendData friendData;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Log.d("susu","name"+snapshot.child("name").getValue(String.class));
                        //  Log.d("susu","email"+snapshot.child("email")); setImageResource(Utils.getProfileImgDrawable(selected));

                        String Fname = snapshot.child("name").getValue(String.class);
                        String Femail = snapshot.child("email").getValue(String.class);
                        String Ftoken = snapshot.child("pushToken").getValue(String.class);
                        Log.d("susu","SS!! name"+Fname+"  / Femail"+Femail);
                        Integer Fimg = snapshot.child("img").getValue(Integer.class);
                       if(Fimg == null)
                           Fimg = 0;
                        //int Fimg= 2;
                        friendData = new FriendData(Fname, Femail, Fimg,Ftoken);

                        items.add(friendData);
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FriendData friendData = items.get(position);

            holder.nameText.setText(friendData.getName());
            holder.emailText.setText(friendData.getEmail());
            holder.img.setImageResource(Utils.getProfileImgDrawable(friendData.getImg()));
            holder.pushToken=friendData.getToken();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView nameText;
            public final TextView emailText;
            public final ImageView img;
            public final Button callBtn;
            public String pushToken;
            //public final TextView ageText;

            public ViewHolder(View view) {
                super(view);

                img = (ImageView) view.findViewById(R.id.image_friend);

                nameText = (TextView) view.findViewById(R.id.name);
                emailText = (TextView) view.findViewById(R.id.email);
                callBtn = (Button) view.findViewById(R.id.but_ar);
                //  ageText = (TextView) view.findViewById(R.id.age);


                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        /*destinationUserModel =new UserModel();
                        destinationUserModel.pushToken=pushToken;
                        destinationUserModel.userName=nameText.getText().toString();*/


                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.app_name)
                                .titleColor(getResources().getColor(R.color.colorPrimary))
                                .content("연락되라 얍")
                                .positiveText("확인")
                                .negativeText("취소")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Bundle bundle = new Bundle();
                                        Intent intent = new Intent(getActivity(),PlayRTCMain.class);
                                        bundle.putString("token",pushToken);
                                        bundle.putString("name",nameText.getText().toString());
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        //sendFCM();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();



                    }
                });
            }
        }


    }

}