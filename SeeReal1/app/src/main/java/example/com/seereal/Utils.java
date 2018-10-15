package example.com.seereal;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

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
            RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(width,height);
            btn_param.addRule(rule1);
            btn_param.addRule(rule2);
            btn.setLayoutParams(btn_param);

            return btn;
        }




}


