package com.aayaffe.sailingracecoursemanager.initializinglayer;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.DistanceType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseException;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 03/06/2017.
 */

public class RaceCourseTiming {
    private static final String TAG = "RaceCourseTiming";

    /**
     * Calculates certain course sailing time according to class and wind.
     * @param b - Boat class
     * @param l - Legs description
     * @param windSpeed - Wind speed in knots
     * @return time in minutes to finish the course in these conditions, negative value if failed
     */
    public static double GetSailTime(Boat b, Legs l, MarkRoundingOrder mro, double dist2m1, double windSpeed){
        double ret = -1;
        if (GeneralUtils.isNull(b,l)||windSpeed<=0)
            return ret;
        if (mro==null) return -1;
        double[] time = new double[3];
        for(Boat.PointOfSail p: Boat.PointOfSail.values()) {
            try {
                double vmg = b.getVmg(windSpeed, p);
                double length = l.GetLength(mro, p, DistanceType.Relative);
                time[p.ordinal()] = length * dist2m1 * vmg;
                length = l.GetLength(mro, p, DistanceType.Absolute);
                time[p.ordinal()] += length * vmg;
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
    /**
     *
     * @param b - boat class descriptor
     * @param wind - wind speed in knots
     * @param targetTime - in minutes
     * @return dist2M1 or -1 if faild
     */
    public static double calcDistByClassWind (Boat b, double wind, double targetTime, Legs l){  //finds the first leg length, since it equals 1 in the factor.
        double ret = -1;
        if (GeneralUtils.isNull(b,l)||wind<=0)
            return ret;
        MarkRoundingOrder mro = l.defaultMarkRounding;
        if (mro==null) return -1;
        double[] Abs = new double[3];
        double[] Rel = new double[3];
        for(Boat.PointOfSail p: Boat.PointOfSail.values()) {
            try {
                double vmg = b.getVmg(wind, p);
                double length = l.GetLength(mro, p, DistanceType.Relative);
                Rel[p.ordinal()] = length * vmg;
                length = l.GetLength(mro, p, DistanceType.Absolute);
                Abs[p.ordinal()] = length * vmg;
            } catch (RaceCourseException e) {
                Log.e(TAG, "Error calculating!", e);
                return -1;
            }
        }
        Log.d(TAG,"Dist 2 M1 calculted is: "+ (targetTime-(Abs[0]+Abs[1]+Abs[2]))/(Rel[0]+Rel[1]+Rel[2]));
        return (targetTime-(Abs[0]+Abs[1]+Abs[2]))/(Rel[0]+Rel[1]+Rel[2]);
    }
}
