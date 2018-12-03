package example.com.seereal;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;

public class Utils {


    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int getProfileImgDrawable(int id) {
        Class<?> drawbleClass = R.drawable.class;
        Field field = null;
        int resId = 0;
        StringBuffer buffer = new StringBuffer("profile_");
        buffer.append(id);

        try {
            field = drawbleClass.getField(buffer.toString());
            resId = field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("Utils", "getProfileImgDrawable: Error");
        }

        return resId;
    }


    public Button create(Context context, String name, int color, int width, int height, int rule1, int rule2) {
        Button btn = new Button(context);
        btn.setId(View.generateViewId());
        btn.setText(name);
        btn.setBackgroundColor(color);
        btn.setTextColor(Color.RED);
        RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(width, height);
        btn_param.addRule(rule1);
        btn_param.addRule(rule2);
        btn.setLayoutParams(btn_param);

        return btn;
    }
    public Button create(Context context, String name, int color, int width, int height, int rule1, int rule2, int rule3, int related_id) {
        Button btn = new Button(context);
        btn.setText(name);
        btn.setBackgroundColor(color);
        btn.setTextColor(Color.RED);
        RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(width, height);
        btn_param.addRule(rule1,rule2);
        btn_param.addRule(rule3,related_id);
        btn.setLayoutParams(btn_param);

        return btn;
    }


    public EditText createET(Context context, int width, int height, int rule1, int rule2) {
        EditText editText = new EditText(context);
        editText.setId(R.id.send_text_view);
        RelativeLayout.LayoutParams et_param = new RelativeLayout.LayoutParams(width, height);
        et_param.addRule(rule1);
        et_param.addRule(rule2);
        editText.setLayoutParams(et_param);

        return editText;
    }


    public TextView createTV(Context context, int width, int height,int rule1, int rule2){
        TextView textView =  new TextView(context);
        //textView.setScroller(new Scroller(context));
        textView.setTextColor(Color.RED);
        RelativeLayout.LayoutParams tv_param = new RelativeLayout.LayoutParams(width,height);
        tv_param.addRule(rule1);
        tv_param.addRule(rule2);
        textView.setTextSize(20);
        textView.setLayoutParams(tv_param);
        return textView;
    }
}
