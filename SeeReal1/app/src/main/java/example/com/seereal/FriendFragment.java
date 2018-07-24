package example.com.seereal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    public static FriendFragment newInstance() {
        FriendFragment friendFragment = new FriendFragment();

        return friendFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.friendListVIew);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mData = new ArrayList<>();
        //ImageView  myImg = null;
        //myImg.setImageDrawable(getResources().getDrawable(R.drawable.but_call2));
        FriendData friendData = new FriendData("재현");
        friendData.setImage(R.drawable.imgeje);
        //  FriendData friendData = new FriendData("수화", "23");
        mData.add(friendData);
        //추가한거

        friendData = new FriendData("수화");
        friendData.setImage(R.drawable.imagesu);
        mData.add(friendData);

        mAdapter = new FriendFragmentAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        return mView;
    }

    class FriendFragmentAdapter extends RecyclerView.Adapter<FriendFragmentAdapter.ViewHolder> {
        private ArrayList<FriendData> items;
        final String packageName="com.PleaseCompany.please";
        Intent intent=getActivity().getPackageManager().getLaunchIntentForPackage(packageName);

        public FriendFragmentAdapter(ArrayList<FriendData> items) {
            this.items = items;
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
            holder.img.setImageResource(friendData.getImg());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView nameText;
            public final ImageView img;
            public final Button callBtn;
            //public final TextView ageText;

            public ViewHolder(View view) {
                super(view);

                img = (ImageView) view.findViewById(R.id.image_friend);
                nameText = (TextView) view.findViewById(R.id.name);
                callBtn=(Button)view.findViewById(R.id.but_ar);
                //  ageText = (TextView) view.findViewById(R.id.age);

                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //getActivity().startActivity(intent);
                        Log.d("PlayRTC","버튼 클릭");
                        Intent i = new Intent(getActivity(),PlayRTCMain.class);
                        startActivity(i);

                    }
                });
            }
        }
    }
}