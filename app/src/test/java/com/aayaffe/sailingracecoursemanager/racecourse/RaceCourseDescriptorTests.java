package com.aayaffe.sailingracecoursemanager.racecourse;

import com.aayaffe.sailingracecoursemanager.BuildConfig;
import com.aayaffe.sailingracecoursemanager.Racecourse.DirDist;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseGate;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseObject;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseStartLine;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;


/**
 * Created by aayaffe on 26/04/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class RaceCourseDescriptorTests {

    @Test
    public void getFirst_CorrectInput_StartLineFirst_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNew();
        RaceCourseObject rco = rcd.getFirst(new AviLocation(0,0),0,100);
        assertThat(rco, instanceOf(RaceCourseStartLine.class));
        assertThat(rco.getType(), is(ObjectTypes.StartLine));
        AviLocation al = rco.getLoc();
        assertEquals(al.lat, new AviLocation(0,0).lat,0.000000001);
        assertEquals(al.lon, new AviLocation(0,0).lon,0.000000001);
    }

    @Test
    public void getNext_Second_CorrectInput_Gate_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNew();
        rcd.getFirst(new AviLocation(0,0),0,100);
        RaceCourseObject rco = rcd.getNext(3000,0,100);
        assertThat(rco, instanceOf(RaceCourseGate.class));
        assertThat(rco.getType(), is(ObjectTypes.Gate));
        AviLocation al = rco.getLoc();
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lat,al.lat,0.0002); //TODO problem with accuracy
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lon,al.lon,0.0001);
    }
    private RaceCourseDescriptor createNew(){
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartLine);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.FinishLine);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(000, 1000));
        dds.add(new DirDist(270, 1000));
        return new RaceCourseDescriptor(os,dds);
    }
}
