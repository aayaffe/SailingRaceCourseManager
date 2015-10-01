package com.aayaffe.sailingracecoursemanager.general;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

}
