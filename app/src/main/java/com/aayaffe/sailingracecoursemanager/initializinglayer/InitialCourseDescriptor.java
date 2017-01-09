package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateConfiguration;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.LocationOptions;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class InitialCourseDescriptor implements Serializable {
    public List<RaceCourseDescriptor2> raceCourseDescriptors;

    public InitialCourseDescriptor(){
        raceCourseDescriptors = new ArrayList<>();

        GenerateTrapezoid60120();
        GenerateTrapezoid70110();
        GenerateWindWardLeeWard();
        GenerateInnerOuterSlalom();

    }

    private void GenerateTrapezoid60120() {
        RaceCourseDescriptor2 trapezoid60120 = new RaceCourseDescriptor2();
        trapezoid60120.name = "Trapezoid 60\\120";
        trapezoid60120.setLastUpdate(new Date());
        trapezoid60120.legDescriptors = new ArrayList<>();

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.67,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk1,mk2,mk3,mk4,finish);

        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.5,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk1,mk2,mk3,mk4,finish);

        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.67,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk1,mk2,mk3,mk4,finish);


        GeneralUtils.addAll(trapezoid60120.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);
        raceCourseDescriptors.add(trapezoid60120);
    }

    private void GenerateTrapezoid70110() {
        RaceCourseDescriptor2 trapezoid70110 = new RaceCourseDescriptor2();
        trapezoid70110.name = "Trapezoid 70\\110";
        trapezoid70110.setLastUpdate(new Date());
        trapezoid70110.legDescriptors = new ArrayList<>();

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk1,mk2,mk3,mk4,finish);

        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.5,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk1,mk2,mk3,mk4,finish);

        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,false,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk1,mk2,mk3,mk4,finish);


        GeneralUtils.addAll(trapezoid70110.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);
        raceCourseDescriptors.add(trapezoid70110);
    }

    private void GenerateWindWardLeeWard() {
        RaceCourseDescriptor2 windwardLeeward = new RaceCourseDescriptor2();
        windwardLeeward.name = "Windward-Leeward";
        windwardLeeward.setLastUpdate(new Date());
        windwardLeeward.legDescriptors = new ArrayList<>();

        Legs windlee = new Legs();
        windlee.marks = new ArrayList<>();
        windlee.name = "Windward-Leeward";
        Mark2 startfinish = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.GATE_CENTER,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(windlee.marks,startfinish,mk1,mk4);


        GeneralUtils.addAll(windwardLeeward.legDescriptors,windlee);
        raceCourseDescriptors.add(windwardLeeward);
    }

    private void GenerateInnerOuterSlalom() {
        RaceCourseDescriptor2 innerOuterSlalom = new RaceCourseDescriptor2();
        innerOuterSlalom.name = "Trapezoid 70\\110 Slalom";
        innerOuterSlalom.setLastUpdate(new Date());
        innerOuterSlalom.legDescriptors = new ArrayList<>();

        Legs shortedOuter = new Legs();
        shortedOuter.marks = new ArrayList<>();
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 s1 = new Mark2("S1",5,new MarkLocation(100,0.5,true,LocationOptions.FROM_LAST_MARK));
        Mark2 s2 = new Mark2("S2",6,new MarkLocation(265,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 s3 = new Mark2("S3",7,new MarkLocation(100,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 finish = new Mark2("Finish",8,new MarkLocation(265,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.GATE_CENTER,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk1,mk2,mk3,mk4,s1,s2,s3,finish);

        GeneralUtils.addAll(innerOuterSlalom.legDescriptors,shortedOuter);
        raceCourseDescriptors.add(innerOuterSlalom);
    }



}
