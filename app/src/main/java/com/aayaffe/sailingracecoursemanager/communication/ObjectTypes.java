package com.aayaffe.sailingracecoursemanager.communication;

import java.io.Serializable;

/**
 * Created by aayaffe on 30/09/2015.
 */
public enum ObjectTypes {
    WorkerBoat,
    FlagBuoy,
    TomatoBuoy,
    TriangleBuoy,
    StartLine,
    FinishLine,
    StartFinishLine,

    RaceManager,  //from here, the relevant ObjectTypes:
    Buoy,
    Gate,
    Satellite,
    ReferencePoint,
    Other
}
