package com.aayaffe.sailingracecoursemanager.racecourse;

import com.aayaffe.sailingracecoursemanager.BuildConfig;
import com.aayaffe.sailingracecoursemanager.Racecourse.DirDist;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseGate;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseObject;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseStartLine;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

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
    public void getFirst_CorrectInput_StartLineFirstAbsDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewAbs();
        RaceCourseObject rco = rcd.getFirst(new AviLocation(0,0),0,100);
        assertThat(rco, instanceOf(RaceCourseStartLine.class));
        assertThat(rco.getType(), is(ObjectTypes.StartLine));
        assertThat(rco.getName(), is("Startline"));
        AviLocation al = rco.getLoc();
        assertEquals(al.lat, new AviLocation(0,0).lat,0.000000001);
        assertEquals(al.lon, new AviLocation(0,0).lon,0.000000001);
    }

    @Test
    public void getNext_Second_CorrectInput_GateAbsDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewAbs();
        rcd.getFirst(new AviLocation(0,0),0,100);
        RaceCourseObject rco = rcd.getNext(3000,0,100);
        assertThat(rco, instanceOf(RaceCourseGate.class));
        assertThat(rco.getType(), is(ObjectTypes.Gate));
        assertThat(rco.getName(), is("Gate1"));

        AviLocation al = rco.getLoc();
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lat,al.lat,0.0002); //TODO problem with accuracy
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lon,al.lon,0.0001);
    }
    @Test
    public void getFirst_CorrectInput_StartLineFirstFracDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewFrac();
        RaceCourseObject rco = rcd.getFirst(new AviLocation(0,0),0,100);
        assertThat(rco, instanceOf(RaceCourseStartLine.class));
        assertThat(rco.getType(), is(ObjectTypes.StartLine));
        assertThat(rco.getName(), is("Startline"));
        AviLocation al = rco.getLoc();
        assertEquals(al.lat, new AviLocation(0,0).lat,0.000000001);
        assertEquals(al.lon, new AviLocation(0,0).lon,0.000000001);
    }

    @Test
    public void getNext_Second_CorrectInput_GateFracDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewFrac();
        rcd.getFirst(new AviLocation(0,0),0,100);
        RaceCourseObject rco = rcd.getNext(2000,0,100);
        assertThat(rco, instanceOf(RaceCourseGate.class));
        assertThat(rco.getType(), is(ObjectTypes.Gate));
        assertThat(rco.getName(), is("Gate1"));
        AviLocation al = rco.getLoc();
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lat,al.lat,0.0002); //TODO problem with accuracy
        assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lon,al.lon,0.0001);
    }
    private RaceCourseDescriptor createNewAbs(){
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartLine);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.FinishLine);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(000, 1000));
        dds.add(new DirDist(270, 1000));

        List<String> names = new ArrayList<>();
        names.add("Startline");
        names.add("Gate1");
        names.add("FinishLine");
        return new RaceCourseDescriptor(os,dds,names);
    }
    private RaceCourseDescriptor createNewFrac(){
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartLine);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.FinishLine);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(0, 0.5f));
        dds.add(new DirDist(270, 0.5f));
        List<String> names = new ArrayList<>();
        names.add("Startline");
        names.add("Gate1");
        names.add("FinishLine");
        return new RaceCourseDescriptor(os,dds,names);

    }
}
