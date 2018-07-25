package com.three_eung.saemoi;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static SimpleDateFormat ym = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
    private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.KOREA);
    private static DecimalFormat df = new DecimalFormat("#,###");

    public static String toCurrencyFormat(int value) {
        return df.format(value);
    }

    public static String toCurrencyString(int value) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(df.format(value));
        buffer.append("원");
        return buffer.toString();
    }

    public static int fromCurrencyFormat(String value) {
        try {
            return (int) df.parse(value);
        } catch (ParseException e) {
        }

        return 0;
    }

    public static String toYearMonth(Date date) {
        return ym.format(date);
    }

    public static String toYearMonth(Calendar calendar) {
        return ym.format(calendar.getTime());
    }

    public static String toYearMonthDay(Calendar calendar) {
        return ymd.format(calendar.getTime());
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static Date stringToDate(String date) {
        Date toParse = new Date();
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
        }

        return null;
    }

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

    public static int getCategoryDrawable(int id) {
        Class<?> drawbleClass = R.drawable.class;
        Field field = null;
        int resId = 0;
        StringBuffer buffer = new StringBuffer("cate_");
        buffer.append(id);

        try {
            field = drawbleClass.getField(buffer.toString());
            resId = field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("Utils", "getCategoryDrawable: Error");
        }

        return resId;
    }
}
