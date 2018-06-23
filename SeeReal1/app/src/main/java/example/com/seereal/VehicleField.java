package example.com.seereal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class VehicleField extends Fragment {

    GridView gridView;
    GridViewAdapter gridViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view, null);
        gridView = view.findViewById(R.id.gridView);

        gridViewAdapter  = new GridViewAdapter(getActivity(),getData());
        gridView.setAdapter(gridViewAdapter);
        return view;
    }

    public ArrayList<CardViewItem> getData()
    {
        ArrayList<CardViewItem> list = new ArrayList<>();

        CardViewItem item = new CardViewItem();
        item.setTitle("자동차");
        item.setTag("그랜져");
        item.setImage(R.drawable.car);
        list.add(item);

        return list;
    }
}
