package com.aayaffe.sailingracecoursemanager.initializinglayer

import `in`.avimarine.orccertificatesimporter.ORCCertObj

object ORCCertHelper {
    /**
     * Calculates an average allowances object of the given certificates.
     * @param certs - List of ORC certificate objects.
     * @return the average allowances object, Null if error
     */
    @JvmStatic
    fun getAvgAllowances(certs: List<ORCCertObj>): Map<String, List<Double>>? {
        val ret: MutableMap<String, List<Double>> = HashMap()
        val s = certs.size
        if (s < 1) {
            return null
        }
        val windSpeeds: List<Double> = certs[0].allowances?.get("WindSpeeds")!!
        ret["WindSpeeds"] = windSpeeds
        val windAngles: List<Double> = certs[0].allowances?.get("WindAngles")!!
        ret["WindAngles"] = windAngles
        var beat: MutableList<Double> = ArrayList()
        var i = 0
        for (c in certs) {
            if (i == 0) {
                beat = c.allowances?.get("Beat")!!
                i++
                continue
            }
            for (n in beat.indices) {
                beat[n] = beat[n] + c.allowances["Beat"]!![n]
            }
        }
        for (n in beat.indices) {
            beat[n] = beat[n] / s
        }
        ret["Beat"] = beat
        val run: List<Double> = ArrayList()
        val beatAngle: List<Double> = ArrayList()
        val gybeAngle: List<Double> = ArrayList()
        val wl: List<Double> = ArrayList()
        val cr: List<Double> = ArrayList()
        val oc: List<Double> = ArrayList()
        val ns: List<Double> = ArrayList()
        val ol: List<Double> = ArrayList()
        val R52: List<Double> = ArrayList()
        ret["R52"] = R52
        val R60: List<Double> = ArrayList()
        val R75: List<Double> = ArrayList()
        val R90: List<Double> = ArrayList()
        val R110: List<Double> = ArrayList()
        val R120: List<Double> = ArrayList()
        val R135: List<Double> = ArrayList()
        val R150: List<Double> = ArrayList()
        return ret
    }
}