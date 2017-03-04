package com.aayaffe.sailingracecoursemanager.initializinglayer;

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
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.5,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk4,mk1,mk2,mk3,finish);

        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.5,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk4,mk1,mk2,mk3,finish);

        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-120,0.67,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk4,mk1,mk2,mk3,finish);

        GeneralUtils.addAll(trapezoid60120.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);

        MarkRoundingOrder I2 = new MarkRoundingOrder("I2",new ArrayList<>(Arrays.asList(0,4,1,4,1,2,3,5)));
        MarkRoundingOrder I3 = new MarkRoundingOrder("I3",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,2,3,5)));
        MarkRoundingOrder I4 = new MarkRoundingOrder("I4",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,4,1,2,3,5)));
        MarkRoundingOrder O2 = new MarkRoundingOrder("O2",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,5)));
        MarkRoundingOrder O3 = new MarkRoundingOrder("O3",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,2,3,5)));
        MarkRoundingOrder O4 = new MarkRoundingOrder("O4",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,2,3,2,3,5)));

        trapezoid60120.markRoundingOptions = new ArrayList<>();
        GeneralUtils.addAll(trapezoid60120.markRoundingOptions,I2,I3,I4,O2,O3,O4);
        trapezoid60120.defaultMarkRounding = I2;

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
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.658,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk4,mk1,mk2,mk3,finish);

        Legs halfBeat = new Legs();
        halfBeat.marks = new ArrayList<>();
        halfBeat.name = "1/2 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.5,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(halfBeat.marks,start,mk4,mk1,mk2,mk3,finish);

        Legs twoThirdsBeat = new Legs();
        twoThirdsBeat.marks = new ArrayList<>();
        twoThirdsBeat.name = "2/3 Beat";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_LAST_MARK));
        mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        finish = new Mark2("Finish",5,new MarkLocation(0,0,true,LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-180,0.032,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk4,mk1,mk2,mk3,finish);
        GeneralUtils.addAll(trapezoid70110.legDescriptors,shortedOuter,halfBeat,twoThirdsBeat);

        MarkRoundingOrder I2 = new MarkRoundingOrder("I2",new ArrayList<>(Arrays.asList(0,4,1,4,1,2,3,5)));
        MarkRoundingOrder I3 = new MarkRoundingOrder("I3",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,2,3,5)));
        MarkRoundingOrder I4 = new MarkRoundingOrder("I4",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,4,1,2,3,5)));
        MarkRoundingOrder O2 = new MarkRoundingOrder("O2",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,5)));
        MarkRoundingOrder O3 = new MarkRoundingOrder("O3",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,2,3,5)));
        MarkRoundingOrder O4 = new MarkRoundingOrder("O4",new ArrayList<>(Arrays.asList(0,4,1,2,3,2,3,2,3,2,3,5)));

        trapezoid70110.markRoundingOptions = new ArrayList<>();
        GeneralUtils.addAll(trapezoid70110.markRoundingOptions,I2,I3,I4,O2,O3,O4);
        trapezoid70110.defaultMarkRounding = I2;

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
        windlee.name = "Windward-Leeward";
        Mark2 startfinish = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        GeneralUtils.addAll(windlee.marks,startfinish,mk4,mk1);

        Legs cruisersWL = new Legs();
        cruisersWL.marks = new ArrayList<>();
        cruisersWL.name = "Cruisers Wind-Lee";
        startfinish = new Mark2("StartFinish",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        mk1 = new Mark2("Mk1",1,new MarkLocation(0,0.5,true, LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",2,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK));
        GeneralUtils.addAll(cruisersWL.marks,startfinish,mk1,mk3);

        GeneralUtils.addAll(windwardLeeward.legDescriptors,windlee,cruisersWL);

//        MarkRoundingOrder L2 = new MarkRoundingOrder("L2",new ArrayList<>(Arrays.asList(0,4,1,4,1,0)));
//        MarkRoundingOrder L3 = new MarkRoundingOrder("L3",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,0)));
//        MarkRoundingOrder L4 = new MarkRoundingOrder("L4",new ArrayList<>(Arrays.asList(0,4,1,4,1,4,1,4,1,0)));
//
//        windwardLeeward.markRoundingOptions = new ArrayList<>();
//        GeneralUtils.addAll(windwardLeeward.markRoundingOptions,L2,L3,L4);
//        windwardLeeward.defaultMarkRounding = L2;

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
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-110,0.67,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,0.5,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 s1 = new Mark2("S1",5,new MarkLocation(100,0.5,true,LocationOptions.FROM_LAST_MARK));
        Mark2 s2 = new Mark2("S2",6,new MarkLocation(265,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 s3 = new Mark2("S3",7,new MarkLocation(100,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 finish = new Mark2("Finish",8,new MarkLocation(265,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.GATE_CENTER,false));
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
        shortedOuter.name = "Shorted Outer";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,1,true,LocationOptions.FROM_LAST_MARK));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(-180,1,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.GATE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 finish = new Mark2("Finish",4,new MarkLocation(10,0.97,true,LocationOptions.FROM_LAST_MARK),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.GATE_CENTER,false));
        GeneralUtils.addAll(shortedOuter.marks,start,mk1,mk2,mk3,finish);

        GeneralUtils.addAll(optimist.legDescriptors,shortedOuter);

        MarkRoundingOrder IOD = new MarkRoundingOrder("IOD",new ArrayList<>(Arrays.asList(0,1,2,3,4)));

        optimist.markRoundingOptions = new ArrayList<>();
        GeneralUtils.addAll(optimist.markRoundingOptions,IOD);
        optimist.defaultMarkRounding = IOD;

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
        cruisersTriangle.name = "Cruisers Triangle";
        Mark2 start = new Mark2("StartFinish",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_FINISH_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        Mark2 mk1 = new Mark2("Mk1",1,new MarkLocation(0,0.5,true,LocationOptions.FROM_MARK_ID,0));
        Mark2 mk2 = new Mark2("Mk2",2,new MarkLocation(-120,1,true,LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3",3,new MarkLocation(120,1,true,LocationOptions.FROM_MARK_ID,2));
        GeneralUtils.addAll(cruisersTriangle.marks,start,mk1,mk2,mk3);

        Legs Triangle = new Legs();
        Triangle.marks = new ArrayList<>();
        Triangle.name = "Olympic Triangle";
        start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE, GateOption.ALWAYS_GATED,-90,0.027, GateReference.RIGHT_MARK,true));
        mk3 = new Mark2("Mk3",1,new MarkLocation(0,0.1,false,LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        mk1 = new Mark2("Mk1",2,new MarkLocation(0,1,true,LocationOptions.FROM_MARK_ID,1),new GateConfiguration(GateType.SATELLITE,GateOption.GATABLE,-125,0.08,GateReference.LEFT_MARK,false));
        mk2 = new Mark2("Mk2",3,new MarkLocation(-135,0.71,true,LocationOptions.FROM_MARK_ID,2));
        Mark2 finish = new Mark2("Finish",4,new MarkLocation(0,0.1,false,LocationOptions.FROM_MARK_ID,2),new GateConfiguration(GateType.FINISH_LINE,GateOption.ALWAYS_GATED,-90,0.032,GateReference.GATE_CENTER,false));
        GeneralUtils.addAll(Triangle.marks,start,mk3,mk1,mk2,finish);


        GeneralUtils.addAll(triangular.legDescriptors,Triangle,cruisersTriangle);
        return triangular;
    }


}
