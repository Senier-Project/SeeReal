package example.com.seereal;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


public class SetButton {

    public Button create(Context context, String name, int color, int width, int height, int rule1, int rule2) {
        Button btn = new Button(context);
        btn.setId(View.generateViewId());
        btn.setText(name);
        btn.setBackgroundColor(color);
        RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(width,height);
        btn_param.addRule(rule1);
        btn_param.addRule(rule2);
        btn.setLayoutParams(btn_param);

        return btn;
    }
}


