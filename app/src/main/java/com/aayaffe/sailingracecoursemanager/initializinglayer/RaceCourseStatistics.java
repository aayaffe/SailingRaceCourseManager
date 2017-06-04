package com.aayaffe.sailingracecoursemanager.initializinglayer;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.DistanceType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseException;

/**
 * Created by aayaffe on 03/06/2017.
 */

public class RaceCourseStatistics {
    private static final String TAG = "RaceCourseStatistics";

    /**
     * Calculates certain course sailing time according to class and wind.
     * @param b - Boat class
     * @param l - Legs description
     * @param windSpeed - Wind speed in knots
     * @return time in minutes to finish the course in these conditions, negative value if failed
     */
    public static double GetSailTime(Boat b, Legs l, MarkRoundingOrder mro, double dist2m1, int windSpeed){
        double ret = -1;
        if (GeneralUtils.isNull(b,l)||windSpeed<=0)
            return ret;
        double[] time = new double[3];
        for(Boat.PointOfSail p: Boat.PointOfSail.values()) {
            try {
                double vmg = b.getVmg(windSpeed, p);
                double length = l.GetLength(mro, p, DistanceType.Relative);
                time[p.ordinal()] = length * dist2m1 * vmg;
                length = l.GetLength(mro, p, DistanceType.Absolute);
                time[p.ordinal()] += length * dist2m1 * vmg;
            } catch (RaceCourseException e) {
                Log.e(TAG, "Error calculating!", e);
                return -1;
            }
        }
        Log.d(TAG,generateCourseStatisticsString(l,time));
        ret = time[0]+time[1]+time[2];
        return ret;
    }

    private static String generateCourseStatisticsString(Legs l, double time[]) {
        String ret = "Course Statistics: Course type: " +
                l.name + ", Upwind: " +
                time[Boat.PointOfSail.UpWind.ordinal()] +
                ", Running: " +
                time[Boat.PointOfSail.Run.ordinal()] +
                ", Reaching: " +
                time[Boat.PointOfSail.Reach.ordinal()];
        return ret;
    }
}
