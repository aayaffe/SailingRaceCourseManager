package com.aayaffe.sailingracecoursemanager.initializinglayer;

import android.support.v7.widget.ActivityChooserView;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.General.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateConfiguration;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateOption;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateReference;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.LocationOptions;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class InitialCourseDescriptor implements Serializable {
    public List<RaceCourseDescriptor2> getRaceCourseDescriptors() {
        return raceCourseDescriptors;
    }

    /***
     * Returns the racecoursedecriptor with the matching name
     * null if none was found.
     * @param name
     * @return
     */
    public RaceCourseDescriptor2 getRaceCourse(String name){
        for (RaceCourseDescriptor2 rcd: getRaceCourseDescriptors()){
            if (rcd.name == name) return rcd;
        }
        return null;
    }

    public List<RaceCourseDescriptor2> raceCourseDescriptors;

    public InitialCourseDescriptor(){
        raceCourseDescriptors = new ArrayList<>();

        raceCourseDescriptors.add(GenerateTrapezoid60120());
        raceCourseDescriptors.add(GenerateTrapezoid70110());
        raceCourseDescriptors.add(GenerateWindWardLeeWard());
        //raceCourseDescriptors.add(GenerateInnerOuterSlalom());
        raceCourseDescriptors.add(GenerateOptimist());
        raceCourseDescriptors.add(GenerateTriangular());

    }

    private RaceCourseDescriptor2 GenerateTrapezoid60120() {
        RaceCourseDescriptor2 trapezoid60120 = new RaceCourseDescriptor2();
        trapezoid60120.name = "Trapezoid 60\\120";
        trapezoid60120.setLastUpdate(new Date());
        trapezoid60120.legDescriptors = new ArrayList<>();
        trapezoid60120.imageID = R.drawable.trapzoid_shorted_inner;

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.markRoundingOptions = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.5,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk4,mk1,mk2,mk3,finish);
        MarkRoundingOrder Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5); //TODO - Cannot calculate last leg!!!!
        MarkRoundingOrder Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        MarkRoundingOrder Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        MarkRoundingOrder Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        MarkRoundingOrder Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        MarkRoundingOrder Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(shortedOuter.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        shortedOuter.defaultMarkRounding = Inner2;



        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.markRoundingOptions = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.5,true,LocationOptions.FROM_MARK_ID,1));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(120,0.1,false,LocationOptions.FROM_MARK_ID,3),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk4,mk1,mk2,mk3,finish);
        Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5);
        Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(halfBeat.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        halfBeat.defaultMarkRounding = Inner2;




        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.markRoundingOptions = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.67,true,LocationOptions.FROM_MARK_ID,1));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(120,0.1,false,LocationOptions.FROM_MARK_ID,3),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk4,mk1,mk2,mk3,finish);
        GeneralUtils.addAll(trapezoid60120.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);
        Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5);
        Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(twoThirdsBeat.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        twoThirdsBeat.defaultMarkRounding = Inner2;

        return trapezoid60120;
    }

    private RaceCourseDescriptor2 GenerateTrapezoid70110() {
        RaceCourseDescriptor2 trapezoid70110 = new RaceCourseDescriptor2();
        trapezoid70110.name = "Trapezoid 70\\110";
        trapezoid70110.setLastUpdate(new Date());
        trapezoid70110.legDescriptors = new ArrayList<>();
        trapezoid70110.imageID = R.drawable.trapzoid_beat_to_finish_inner_startfinish;

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.markRoundingOptions = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.658,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk4,mk1,mk2,mk3,finish);
        MarkRoundingOrder Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5); //TODO - Cannot calculate last leg!!!!
        MarkRoundingOrder Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        MarkRoundingOrder Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        MarkRoundingOrder Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        MarkRoundingOrder Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        MarkRoundingOrder Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(shortedOuter.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        shortedOuter.defaultMarkRounding = Inner2;


        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.markRoundingOptions = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.5,true,LocationOptions.FROM_MARK_ID,1));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(110,0.1,false,LocationOptions.FROM_MARK_ID,3),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk4,mk1,mk2,mk3,finish);
        Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5);
        Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(halfBeat.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        halfBeat.defaultMarkRounding = Inner2;

        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.markRoundingOptions = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_MARK_ID,1));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(110,0.1,false,LocationOptions.FROM_MARK_ID,3),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk4,mk1,mk2,mk3,finish);
        Inner2 = new MarkRoundingOrder("I2",0,4,1,4,1,2,3,5);
        Inner3 = new MarkRoundingOrder("I3",0,4,1,4,1,4,1,2,3,5);
        Inner4 = new MarkRoundingOrder("I4",0,4,1,4,1,4,1,4,1,2,3,5);
        Outer2 = new MarkRoundingOrder("O2",0,4,1,2,3,2,3,5);
        Outer3 = new MarkRoundingOrder("O3",0,4,1,2,3,2,3,2,3,5);
        Outer4 = new MarkRoundingOrder("O4",0,4,1,2,3,2,3,2,3,2,3,5);
        GeneralUtils.addAll(twoThirdsBeat.markRoundingOptions,Inner2,Inner3, Inner4,Outer2,Outer3,Outer4);
        twoThirdsBeat.defaultMarkRounding = Inner2;

        GeneralUtils.addAll(trapezoid70110.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);

        return trapezoid70110;
    }

    private RaceCourseDescriptor2 GenerateWindWardLeeWard() {
        RaceCourseDescriptor2 windwardLeeward = new RaceCourseDescriptor2();
        windwardLeeward.name = "Windward-Leeward";
        windwardLeeward.setLastUpdate(new Date());
        windwardLeeward.legDescriptors = new ArrayList<>();
        windwardLeeward.imageID = R.drawable.windward_leeward;

        Legs windlee = new Legs();
        windlee.marks = new ArrayList<>();
        windlee.markRoundingOptions = new ArrayList<>();
        windlee.name = "L";
        Mark2 startfinish = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(windlee.marks,startfinish,mk4,mk1);
        MarkRoundingOrder L2 = new MarkRoundingOrder("L2",0,4,1,4,1,4,0);
        MarkRoundingOrder L3 = new MarkRoundingOrder("L3",0,4,1,4,1,4,1,4,0);
        MarkRoundingOrder L4 = new MarkRoundingOrder("L4",0,4,1,4,1,4,1,4,1,4,0);
        GeneralUtils.addAll(windlee.markRoundingOptions,L2,L3, L4);
        windlee.defaultMarkRounding = L2;

        Legs cruisersWL = new Legs();
        cruisersWL.marks = new ArrayList<>();
        cruisersWL.markRoundingOptions = new ArrayList<>();
        cruisersWL.name = "Cruisers Wind-Lee";
        startfinish = new Mark2("StartFinish",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,0.5,true, LocationOptions.FROM_MARK_ID,0));
        Mark2 mk3 = new Mark2("Mk3",2,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,1));
        GeneralUtils.addAll(cruisersWL.marks,startfinish,mk1,mk3);
        L2 = new MarkRoundingOrder("L2",0,4,1,4,1,4,0); //TODO: Unable to calculate...!!!
        L3 = new MarkRoundingOrder("L3",0,4,1,4,1,4,1,4,0);
        L4 = new MarkRoundingOrder("L4",0,4,1,4,1,4,1,4,1,4,0);
        GeneralUtils.addAll(cruisersWL.markRoundingOptions,L2,L3, L4);
        windlee.defaultMarkRounding = L2;

        GeneralUtils.addAll(windwardLeeward.legDescriptors,windlee,cruisersWL);
        return windwardLeeward;
    }

    private RaceCourseDescriptor2 GenerateInnerOuterSlalom() {
        RaceCourseDescriptor2 innerOuterSlalom = new RaceCourseDescriptor2();
        innerOuterSlalom.name = "Trapezoid 70\\110 Slalom";
        innerOuterSlalom.setLastUpdate(new Date());
        innerOuterSlalom.legDescriptors = new ArrayList<>();
        innerOuterSlalom.imageID = R.drawable.trapzoid_slalum_inner;

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,4),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 s1 = new Mark2("S1",5,new MarkLocation(100,0.5,true,LocationOptions.FROM_MARK_ID,3));
        Mark2 s2 = new Mark2("S2",6,new MarkLocation(265,1,true,LocationOptions.FROM_MARK_ID,5));
        Mark2 s3 = new Mark2("S3",7,new MarkLocation(100,1,true,LocationOptions.FROM_MARK_ID,6));
        Mark2 finish = new Mark2("Finish",8,new MarkLocation(265,1,true,LocationOptions.FROM_MARK_ID,7),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.GATE_CENTER,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk4,mk1,mk2,mk3,s1,s2,s3,finish);

        GeneralUtils.addAll(innerOuterSlalom.legDescriptors,shortedOuter);
        return innerOuterSlalom;
    }

    /**
     * Verified according to ISAF RACE MANAGEMENT MANUAL November 2011
      * @return IOD Optimist race course
     */
    private RaceCourseDescriptor2 GenerateOptimist() {
        RaceCourseDescriptor2 optimist = new RaceCourseDescriptor2();
        optimist.name = "Optimist Course";
        optimist.setLastUpdate(new Date());
        optimist.legDescriptors = new ArrayList<>();
        optimist.imageID = R.drawable.optimist;

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.markRoundingOptions = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,0));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,1,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.GATE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",4,new MarkLocation(135,0.027,false,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-35,0.032,GateReference.GATE_CENTER,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk1,mk2,mk3,finish);
        MarkRoundingOrder IOD = new MarkRoundingOrder("IOD",0,1,2,3,4);
        GeneralUtils.addAll(shortedOuter.markRoundingOptions,IOD);
        shortedOuter.defaultMarkRounding = IOD;

        GeneralUtils.addAll(optimist.legDescriptors,shortedOuter);
        return optimist;
    }

    /**
     * @return
     */
    private RaceCourseDescriptor2 GenerateTriangular() {
        RaceCourseDescriptor2 triangular = new RaceCourseDescriptor2();
        triangular.name = "Triangular";
        triangular.setLastUpdate(new Date());
        triangular.legDescriptors = new ArrayList<>();
        triangular.imageID = R.drawable.triangular;

        Legs cruisersTriangle = new Legs();
        cruisersTriangle.marks = new ArrayList<>();
        cruisersTriangle.markRoundingOptions = new ArrayList<>();
        cruisersTriangle.name = "Cruisers Triangle";
        Mark2 start = new Mark2("StartFinish",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,0.5,true,LocationOptions.FROM_MARK_ID,0));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,1,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(120,1,true,LocationOptions.FROM_MARK_ID,2));
        GeneralUtils.addAll(cruisersTriangle.marks,start,mk1,mk2,mk3);
        MarkRoundingOrder IOD = new MarkRoundingOrder("IOD",0,1,2,3,4); //TODO: Unable to calculate
        GeneralUtils.addAll(cruisersTriangle.markRoundingOptions,IOD);
        cruisersTriangle.defaultMarkRounding = IOD;

        Legs Triangle = new Legs();
        Triangle.marks = new ArrayList<>();
        Triangle.markRoundingOptions = new ArrayList<>();
        Triangle.name = "Olympic Triangle";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        mk3 = new Mark2("Mk3",3,new MarkLocation(0,0.1,false,LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,3),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-135,0.71,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 finish = new Mark2("Finish",4,new MarkLocation(0,0.1,false,LocationOptions.FROM_MARK_ID,1),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.GATE_CENTER,true));
        GeneralUtils.addAll(Triangle.marks,start,mk3,mk1,mk2,finish);
        MarkRoundingOrder TW2 = new MarkRoundingOrder("TW2",0,1,2,3,4); //TODO: Unable to calculate, add all TW and TL
        GeneralUtils.addAll(Triangle.markRoundingOptions,IOD);
        Triangle.defaultMarkRounding = IOD;

        GeneralUtils.addAll(triangular.legDescriptors,Triangle,cruisersTriangle);
        return triangular;
    }


}
