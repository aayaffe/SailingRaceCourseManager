package com.aayaffe.sailingracecoursemanager.map;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;


import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

/**
 * Created by aayaffe on 04/10/2015.
 */
public class OpenSeaMap {
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;

    private static final String MAPFILE = "germany.map";

    public void MapInit(Context c, MapView mv)
    {
//        if (mapView==null) {
//            mapView = new MapView(c);
//            this.mapView.setClickable(true);
//            this.mapView.getMapScaleBar().setVisible(false);
//            this.mapView.setBuiltInZoomControls(false);
//            this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
//            this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);
//
//            tileCache = AndroidUtil.createTileCache(c, "mapcache",
//                    mapView.getModel().displayModel.getTileSize(), 1f,
//                    this.mapView.getModel().frameBufferModel.getOverdrawFactor());
//        }
//        return mapView;
        mapView = mv;

    }

    public void setCenter(Location l){
        setCenter(GeoUtils.toLatLong(l));
    }
    public void setCenter(LatLong ll){
        mapView.getModel().mapViewPosition.setCenter(ll);
    }
    public void setCenter(double lat, double lon){
        setCenter(new LatLong(lat, lon));
    }
    public void setZoomLevel(int zoom){
        mapView.getModel().mapViewPosition.setZoomLevel((byte) zoom);
    }
    public void destroy(){
        mapView.destroyAll();
    }
    public Marker addMark(Marker m){
        mapView.getLayerManager().getLayers().add(m);
        return m;
    }
    public boolean contains(Marker m){
        return mapView.getLayerManager().getLayers().contains(m);
    }
    public Marker removeMark(Marker m){
        mapView.getLayerManager().getLayers().remove(m);
        return m;
    }
    private void zoomToBounds(LatLong ll1, LatLong ll2)
    {
        BoundingBox bb = getBoundingBox(ll1,ll2);
        Dimension dimension = this.mapView.getModel().mapViewDimension.getDimension();
        this.mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
                bb.getCenterPoint(),
                LatLongUtils.zoomForBounds(dimension, bb, this.mapView.getModel().displayModel.getTileSize())));
    }
    private BoundingBox getBoundingBox(LatLong ll1, LatLong ll2){
        double minLat,maxLat,minLon,maxLon;
        if (ll1.latitude<ll2.latitude)
        {
            minLat = ll1.latitude;
            maxLat = ll2.latitude;
        }
        else {
            minLat = ll2.latitude;
            maxLat = ll1.latitude;
        }
        if (ll1.longitude<ll2.longitude)
        {
            minLon = ll1.longitude;
            maxLon = ll2.longitude;
        }
        else {
            minLon = ll2.longitude;
            maxLon = ll1.longitude;
        }
        BoundingBox bb = new BoundingBox(minLat,
                minLon, maxLat, maxLon);
        return bb;
    }


    public void loadMap(){
        // tile renderer layer using internal render theme
        MapDataStore mapDataStore = new MapFile(getMapFile());
        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
//         only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    private File getMapFile() {
        File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
        return file;
    }


}
