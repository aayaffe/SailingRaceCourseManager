package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.avimarine.orccertificatesimporter.ORCCertObj;

import static org.junit.Assert.*;

public class ORCCertHelperTest {

    JSONObject testJson;
    public static final String UTF8_BOM = "\uFEFF";

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
    @Before
    public void setUp() {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.json");
            String content = CharStreams.toString(new InputStreamReader(
                    in, Charsets.UTF_8));
            content = removeUTF8BOM(content);
            testJson = new JSONObject(content.trim());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getAvgAllowances() throws JSONException {
        JSONArray certsJson = testJson.getJSONArray("rms");
        ArrayList<ORCCertObj> certs = new ArrayList<>();
        certs.add(ORCCertObj.fromJSON(certsJson.getJSONObject(0)));
        certs.add(ORCCertObj.fromJSON(certsJson.getJSONObject(1)));

        Map<String, List<Double>> a = ORCCertHelper.getAvgAllowances(certs);

        assertEquals(732.35, a.get("Beat").get(0), 0.01);
        assertEquals(353.6, a.get("R52").get(4), 0.01);
    }

}