package com.aayaffe.sailingracecoursemanager.Initializing_Layer;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 16/08/2016.
 */
public class BoatXmlParser {
    private static final String TAG = "BoatXmlParser";
    private Context context;
    private String url;
    private XmlPullParser parser;
    private List<String> names;


    public BoatXmlParser(Context context, String url) {
        this.context = context;
        this.url = url;
    }
    public List<Boat> parseBoats() {
        /*Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {*/
        List<Boat> boats = new ArrayList<>();

        try {
            InputStream stream = context.getApplicationContext().getAssets().open(url);
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            parser = xmlFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);

            boats = getBoats(parser);
            stream.close();
        } catch (Exception e) {
            Log.e(TAG, "parse boats error",e);
        }/*
            }
        });
        thread.start();*/
        return boats;
    }
    private List<Boat> getBoats(XmlPullParser xmlPullParser) {
        List<Boat> boats = new ArrayList<>();
        double[][] vmg = new double[4][3]; //[upwind,downwind,reach][5+,8+,12+,15+]
        int event;
        try {
            event = xmlPullParser.getEventType();
            String attributeHolder;
            String name;
            int windIndex=0;
            String valueHolder="0";
            while (event != XmlPullParser.END_DOCUMENT) {
                name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "Boat":
                                attributeHolder = safeAttributeValue("name");
                                boats.add(new Boat(attributeHolder));
                                vmg = new double[4][3];
                                break;
                            case "Wind5":
                                windIndex = 0;
                                break;
                            case "Wind8":
                                windIndex = 1;
                                break;
                            case "Wind12":
                                windIndex = 2;
                                break;
                            case "Wind15":
                                windIndex = 3;
                                break;
                        }

                        break;
                    case XmlPullParser.TEXT:
                        valueHolder=safeTextValue();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (name){
                            case "Upwind":
                                vmg[windIndex][0]=Double.parseDouble(valueHolder);
                                break;
                            case "Downwind":
                                vmg[windIndex][1]=Double.parseDouble(valueHolder);
                                break;
                            case "Reach":
                                vmg[windIndex][2]=Double.parseDouble(valueHolder);
                                break;
                            case "TargetTime":
                                boats.get(boats.size()-1).setTargettime(Integer.parseInt(valueHolder));
                                break;
                            case "Boat":
                                boats.get(boats.size()-1).setVmg(vmg);
                                break;
                        }
                        break;
                }
                event = xmlPullParser.next();
            }
        } catch (Exception e) {
            Log.e(TAG,"getBoats error",e);
        }
        return boats;
    }


    private String safeAttributeValue(String keyName) {
        String value = parser.getAttributeValue(null, keyName);
        if (value != null) return value;
        Log.w("boat xml parser", "null attribute for keyName: " + keyName);
        return "_";  // TODO: the '_' char is just for debug, remove before use.
    }
    private String safeTextValue() {
        String value = parser.getText();
        if (value != null) return value;
        Log.w("boat xml parser", "null text found");
        return "_";  // TODO: the '_' char is just for debug, remove before use.
    }
}
