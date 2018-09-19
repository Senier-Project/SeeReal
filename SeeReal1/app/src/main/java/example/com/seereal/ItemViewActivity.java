package example.com.seereal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemViewActivity extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        //TODO : firebase로부터 img, title, position가져오기 - VehicleField.java 에서호출
        intent = getIntent();
        int position = intent.getIntExtra("position",0);

        ImageView imgV = findViewById(R.id.img);
        TextView title = findViewById(R.id.title);
        TextView tag = findViewById(R.id.tag);
        TextView user = findViewById(R.id.user);

        if(position == 0)
        {// String imageName = "car1.png";

            imgV.setImageResource(R.drawable.tire);
            title.setText("타이어 지렁이 수리");
            tag.setText("#타이어#나갔음#지렁이#수리#짱");
            user.setText("usuhwa2@gmail.com");

        }else{//3
            imgV.setImageResource(R.drawable.car);
            title.setText("자동차 본넷");
            tag.setText("#그랜져#자동차#본넷#고장#실화임?");
            user.setText("usuhwa2@gmail.com");
        }

    }
}
