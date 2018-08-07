package example.com.seereal;

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

public class VehicleField extends Fragment {

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

        SNSData snsData = new SNSData(5, "자동차 본넷","#그랜져#자동차#본넷#고장#실화임?");


        CardViewItem item = new CardViewItem();

        item.setTitle(snsData.getTitle());
        item.setImage(Utils.getProfileImgDrawable(snsData.getImg()));
        item.setTag(snsData.getTag());

        item.setTitle("자동차 본넷");
        item.setTag("#그랜져#자동차#본넷#고장#실화임?");
        item.setImage(R.drawable.car);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        item = new CardViewItem();
        item.setTitle("타이어 지렁이 수리");
        item.setTag("#타이어#나갔음#지렁이#수리#짱");
        item.setImage(R.drawable.tire);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        item = new CardViewItem();
        item.setTitle("타이어 교체");
        item.setTag("#타이어#교체#이렇게#하세요#넘나#힘들다");
        item.setImage(R.drawable.fixed_tire);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        item = new CardViewItem();
        item.setTitle("자동차 배터리");
        item.setTag("#자동차#배터리#교체#이렇게#하세요");
        item.setImage(R.drawable.car_battery);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        item = new CardViewItem();
        item.setTitle("타이어 마모 상태 확인");
        item.setTag("#타이어#마모상태#성공적");
        item.setImage(R.drawable.tired);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);

        item = new CardViewItem();
        item.setTitle("사이드 미러 교체");
        item.setTag("#사이드_미러#직접#교체#예랑이#해줘따");
        item.setImage(R.drawable.side_mirror);
        item.setIsLike(R.drawable.baseline_favorite_border_black_18dp);
        list.add(item);


        return list;
    }
}
