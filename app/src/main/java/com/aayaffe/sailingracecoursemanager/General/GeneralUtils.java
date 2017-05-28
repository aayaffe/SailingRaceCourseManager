package com.aayaffe.sailingracecoursemanager.General;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 02/10/2015.
 */
public class GeneralUtils {
    public static String TAG = "GeneralUtils";
    public static Date parseDate(String dateTime){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.US);
        Date date = null;
        try {
            date = format.parse(dateTime);
        }catch(ParseException e){
            Log.e(TAG,"Date parse exception", e);
        }
        return date;
    }
    public static long dateDifference(Date dateTime){

        if (dateTime==null)
            return -1;
        Date currentDate = new Date(System.currentTimeMillis());

        long diffInMs = currentDate.getTime() - dateTime.getTime();

        return TimeUnit.MILLISECONDS.toSeconds(diffInMs);

    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    public static int getDeviceWidth(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return screenWidth;
    }
    public static int getDeviceHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        return screenHeight;
    }

    public static boolean isValid(String text, Class type, Float min, Float max) {
        if (text==null||text.isEmpty()){
            return false;
        }
        CLAZZ z = CLAZZ.valueOf(type.getSimpleName());
        if (z==null)
            return false;
        switch (z) {
            case Float:
                Float f = tryParseFloat(text);
                return f != null && isInBounds(f, min, max);
            case Double:
                Double d = tryParseDouble(text);
                return d != null && isInBounds(d, min, max);
            case Integer:
                Integer i = tryParseInt(text);
                return i != null && isInBounds(i, min, max);
            default:
                return false;
        }
    }
    public static boolean isInBounds(Number n, Float min, Float max){
        if (n==null)
            return false;
        if ((min!=null) && (n.floatValue()<min))
                return false;
        if ((max!=null)&&(n.floatValue()>max))
                return false;
        return true;
    }

    public static <T> void addAll(List<T> list, T... elements) {
        if (list!=null)
        {
            Collections.addAll(list, elements);
        }
    }

    enum CLAZZ {
        Integer,Double,Float;

    }


    public static Float tryParseFloat(String val){
        if (val==null||val.isEmpty())
            return null;
        try{
            return Float.parseFloat(val);
        }catch (Exception e){
            Log.e(TAG, "Failed to parse Float",e);
            return null;
        }
    }
    public static Integer tryParseInt(String val){
        try{
            return Integer.parseInt(val);
        }catch (Exception e){
            Log.e(TAG, "Failed to parse Integer",e);
            return null;
        }
    }
    public static Double tryParseDouble(String val){
        try{
            return Double.parseDouble(val);
        }catch (Exception e){
            Log.e(TAG, "Failed to parse Double",e);
            return null;
        }
    }

    public static boolean isNull (Object... objects){
        for (Object o : objects){
            if (o==null)
                return true;
        }
        return false;
    }
}
