package example.com.seereal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
        FriendData friendData = new FriendData("수화");
      //  FriendData friendData = new FriendData("수화", "23");
        mData.add(friendData);

        //추가한거

        friendData = new FriendData("수화22");
        friendData.setImage(R.drawable.imagesu);
        mData.add(friendData);

        mAdapter = new FriendFragmentAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        return mView;
    }

    class FriendFragmentAdapter extends RecyclerView.Adapter<FriendFragmentAdapter.ViewHolder> {
        private ArrayList<FriendData> items;

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
            //public final TextView ageText;

            public ViewHolder(View view) {
                super(view);

                img = (ImageView) view.findViewById(R.id.image_friend);
                nameText = (TextView) view.findViewById(R.id.name);
              //  ageText = (TextView) view.findViewById(R.id.age);
            }
        }
    }
}
