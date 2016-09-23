package com.aayaffe.sailingracecoursemanager.Racecourse;

import android.support.annotation.Nullable;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.firebase.database.Exclude;

import java.util.Iterator;
import java.util.List;

/**
 * Created by aayaffe on 22/04/2016.
 */
public  class RaceCourseDescriptorGeneral{
    private String type;
    public List<ObjectTypes> objects;
    List<DirDist> dirDist;
    List<String> names;
    @Exclude
    public List<ObjectTypes> getObjects() {
        return objects;
    }
    @Exclude
    public void setObjects(List<ObjectTypes> objects) {
        this.objects = objects;
    }
    public List<DirDist> getDirDist() {
        return dirDist;
    }
    public void setDirDist(List<DirDist> dirDist) {
        this.dirDist = dirDist;
    }
    public List<String> getNames() {
        return names;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

//    /***
//     * Used for firebase serialization only
//     * @return
//     */
//
//    public List<int> getIntObjects() {
//        return objects;
//    }
//    public void setIntObjects(List<int> objects) {
//        this.objects = objects;
//    }

    public RaceCourseDescriptorGeneral(String type, List<ObjectTypes> objects, List<DirDist> dirDistList, List<String> names){
        this.type = type;
        this.objects = objects;
        this.dirDist = dirDistList;
        this.names = names;
    }

}
