package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by aayaffe on 10/01/2017.
 */
@RunWith(CustomRobolectricRunner.class)
public class GeoUtilsTest {
    @Test
    public void getLocationFromDirDist() throws Exception {
        AviLocation loc1 = new AviLocation();
        int distInMeters = 1852;
        loc1.setLat(32.0);
        loc1.setLon(32.0);
        AviLocation loc2 = GeoUtils.getLocationFromDirDist(loc1,15f,distInMeters);

        float dist = (int)loc1.distanceTo(loc2);

        Assert.assertEquals("Distance difference is " + Math.abs(100 - (dist/(float)distInMeters)*100) + "%" ,distInMeters,dist,6);
    }



//    @Test
//    public void getLocationFromDirDist1() throws Exception {
//        Location loc1 = new Location("test");
//        int distInMeters = 10;
//        loc1.setLatitude(60.0);
//        loc1.setLongitude(60.0);
//        Location loc2 = GeoUtils.getLocationFromDirDistm(loc1,174f,distInMeters);
//        float dist = (int)loc1.distanceTo(loc2);
//        Assert.assertEquals("Distance difference is " + Math.abs(100 - (dist/1000.0/(float)distInMeters)*100) + "%" ,distInMeters,dist/1000.0,0.3);
//    }

    @Test
    public void getLocationFromDirDist2() throws Exception {

    }

    @Test
    public void getLocationFromTriangulation() throws Exception {

    }

    @Test
    public void getMidPointLocation() throws Exception {

    }

    @Test
    public void toHours_CorrectInput_ReturnsCorrect() {
        assertThat(GeoUtils.toHours(360), is(6.0));
    }

}