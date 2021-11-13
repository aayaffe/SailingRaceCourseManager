package com.aayaffe.sailingracecoursemanager.initializinglayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.avimarine.orccertificatesimporter.ORCCertObj;

public class ORCCertHelper {

    /**
     * Calculates an average allowances object of the given certificates.
     * @param certs - List of ORC certificate objects.
     * @return the average allowances object, Null if error
     */
    public static Map<String, List<Double>> getAvgAllowances(List<ORCCertObj> certs){
        Map<String, List<Double>> ret = new HashMap<>();
        int s = certs.size();
        if (s<1){
            return null;
        }
        List<Double> windSpeeds = certs.get(0).getAllowances().get("WindSpeeds");
        ret.put("WindSpeeds", windSpeeds);
        List<Double> windAngles = certs.get(0).getAllowances().get("WindAngles");
        ret.put("WindAngles", windAngles);
        List<Double> beat = new ArrayList<>();
        int i =0 ;
        for (ORCCertObj c : certs){
            if (i == 0){
                beat = c.getAllowances().get("Beat");
                i++;
                continue;
            }
            for (int n=0;n < beat.size();n++) {
                beat.set(n, beat.get(n)+c.getAllowances().get("Beat").get(n).doubleValue());
            }
        }
        for (int n=0;n < beat.size();n++){
            beat.set(n,beat.get(n)/s);
        }
        ret.put("Beat", beat);


        List<Double> run = new ArrayList<>();
        List<Double> beatAngle = new ArrayList<>();
        List<Double> gybeAngle = new ArrayList<>();
        List<Double> wl = new ArrayList<>();
        List<Double> cr = new ArrayList<>();
        List<Double> oc = new ArrayList<>();
        List<Double> ns = new ArrayList<>();
        List<Double> ol = new ArrayList<>();
        List<Double> R52 = new ArrayList<>();
        ret.put("R52",R52);
        List<Double> R60 = new ArrayList<>();
        List<Double> R75 = new ArrayList<>();
        List<Double> R90 = new ArrayList<>();
        List<Double> R110 = new ArrayList<>();
        List<Double> R120 = new ArrayList<>();
        List<Double> R135 = new ArrayList<>();
        List<Double> R150 = new ArrayList<>();

        return ret;
    }

}
