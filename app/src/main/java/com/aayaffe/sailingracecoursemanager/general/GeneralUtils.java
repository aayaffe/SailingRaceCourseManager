package com.aayaffe.sailingracecoursemanager.general;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by aayaffe on 02/10/2015.
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
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
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

}
