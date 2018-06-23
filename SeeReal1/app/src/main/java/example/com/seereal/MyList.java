package example.com.seereal;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

public class MyList extends Fragment {

    GridView gridView;
    GridViewAdapter gridViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view, null);
        gridView = view.findViewById(R.id.gridView);

        gridViewAdapter  = new GridViewAdapter(getActivity(),getData(),MainActivity.width,MainActivity.height);
        gridView.setAdapter(gridViewAdapter);


        EditText editText = view.findViewById(R.id.vehicleSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();

                ((GridViewAdapter)gridView.getAdapter()).getFilter().filter(filterText);

            }
        });
        return view;
    }

    public ArrayList<CardViewItem> getData()
    {
        ArrayList<CardViewItem> list = new ArrayList<>();

        CardViewItem item = new CardViewItem();
        item.setTitle("사이드 미러 교체");
        item.setTag("#사이드_미러#직접#교체#예랑이#해줘따");
        item.setImage(R.drawable.side_mirror);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);


        return list;
    }

}
