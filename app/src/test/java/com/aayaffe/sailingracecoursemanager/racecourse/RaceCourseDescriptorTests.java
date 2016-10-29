package com.aayaffe.sailingracecoursemanager.racecourse;

import com.aayaffe.sailingracecoursemanager.BuildConfig;
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

        RaceCourseDescriptor rcd = createNewAbs(new AviLocation(0,0),0,100,2000);
        for(RaceCourseObject rco: rcd){
            assertThat(rco, instanceOf(RaceCourseStartLine.class));
            assertThat(rco.getType(), is(ObjectTypes.StartLine));
            assertThat(rco.getName(), is("Startline"));
            AviLocation al = rco.getLoc();
            assertEquals(al.lat, new AviLocation(0,0).lat,0.000000001);
            assertEquals(al.lon, new AviLocation(0,0).lon,0.000000001);
            break;
        }

    }

    @Test
    public void getNext_Second_CorrectInput_GateAbsDistance_ReturnsCorrect() {

        RaceCourseDescriptor rcd = createNewAbs(new AviLocation(0,0),0,100,2000);
        int c = 0;
        for(RaceCourseObject rco: rcd) {
            if(c==1){
                assertThat(rco, instanceOf(RaceCourseGate.class));
                assertThat(rco.getType(), is(ObjectTypes.Gate));
                assertThat(rco.getName(), is("Gate1"));
                AviLocation al = rco.getLoc();
                assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lat,al.lat,0.0002); //TODO problem with accuracy
                assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lon,al.lon,0.0001);
            }
            c++;
        }



    }
    @Test
    public void getFirst_CorrectInput_StartLineFirstFracDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewFrac(new AviLocation(0,0),0,100,2000);
        for(RaceCourseObject rco: rcd){
            assertThat(rco, instanceOf(RaceCourseStartLine.class));
            assertThat(rco.getType(), is(ObjectTypes.StartLine));
            assertThat(rco.getName(), is("Startline"));
            AviLocation al = rco.getLoc();
            assertEquals(al.lat, new AviLocation(0,0).lat,0.000000001);
            assertEquals(al.lon, new AviLocation(0,0).lon,0.000000001);
            break;
        }
    }

    @Test
    public void getNext_Second_CorrectInput_GateFracDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewFrac(new AviLocation(0,0),0,100,2000);
        int c = 0;
        for(RaceCourseObject rco: rcd) {
            if(c==1){
                assertThat(rco, instanceOf(RaceCourseGate.class));
                assertThat(rco.getType(), is(ObjectTypes.Gate));
                assertThat(rco.getName(), is("Gate1"));
                AviLocation al = rco.getLoc();
                assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lat,al.lat,0.0002); //TODO problem with accuracy
                assertEquals(new AviLocation(0.00888888888888888888888888888889,0).lon,al.lon,0.0001);
            }
            c++;
        }
    }


    @Test
    public void DiffWindDir_CorrectInput_GateFracDistance_ReturnsCorrect() {
        RaceCourseDescriptor rcd = createNewFrac(new AviLocation(0,0),90,100,2000);
        int c = 0;
        for(RaceCourseObject rco: rcd) {
            if (c==0){
                assertThat(rco, instanceOf(RaceCourseStartLine.class));
                assertThat(rco.getType(), is(ObjectTypes.StartLine));
                assertThat(rco.getName(), is("Startline"));
                AviLocation al = rco.getLoc();
                assertEquals(al.lat, new AviLocation(0,0).lat,0.00001);
                assertEquals(al.lon, new AviLocation(0,0).lon,0.00001);
            }
            if(c==1){
                assertThat(rco, instanceOf(RaceCourseGate.class));
                assertThat(rco.getType(), is(ObjectTypes.Gate));
                assertThat(rco.getName(), is("Gate1"));
                AviLocation al = rco.getLoc();
                assertEquals(new AviLocation(0,0.0088888888888).lat,al.lat,0.0002); //TODO problem with accuracy
                assertEquals(new AviLocation(0,0.0088888888888).lon,al.lon,0.0002);
            }
            c++;
        }
    }

    private RaceCourseDescriptor createNewAbs(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength){
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
        return new RaceCourseDescriptor("newAbs", os,dds,names,startlineLoc,windDir,startLineLength,commonLength);
    }
    private RaceCourseDescriptor createNewFrac(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength){
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
        return new RaceCourseDescriptor("newAbs", os,dds,names,startlineLoc,windDir,startLineLength,commonLength);


    }
}
