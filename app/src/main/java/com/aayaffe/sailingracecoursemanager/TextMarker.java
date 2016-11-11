//package com.aayaffe.sailingracecoursemanager;
//
///**
// * Created by aayaffe on 23/09/2015.
// */
//import org.mapsforge.core.graphics.Bitmap;
//import org.mapsforge.core.graphics.Canvas;
//import org.mapsforge.core.graphics.Paint;
//
//import org.mapsforge.core.model.BoundingBox;
//
//import org.mapsforge.core.model.LatLong;
//import org.mapsforge.core.model.Point;
//import org.mapsforge.core.model.Rectangle;
//import org.mapsforge.core.util.MercatorProjection;
//import org.mapsforge.map.layer.overlay.Marker;
//
///**
// * Created with IntelliJ IDEA.
// * User: stanimir
// * Date: 2/6/14
// * Time: 9:52 AM
// * developer STANIMIR MARINOV
// */
//
//public class TextMarker extends Marker {
//
//    private String text;
//
//    private Paint paint;
//    private int offsetX = 0;
//    private int offsetY = 0;
//
//    /**
//
//     * @param txt      the title
//     * @param paint    The paint used for the text (e.g. color, size, style)
//
//     * @param geoPoint the initial geographical coordinates of this marker (may be null).
//     * @param drawable the initial {@code Drawable} of this marker (may be null).
//     */
//
//    public TextMarker(String txt, Paint paint, LatLong geoPoint, Bitmap drawable) {
//
//        super(geoPoint, drawable,0,0);
//        this.text = txt;
//
//        this.paint = paint;
//
//    }
//
//    @Override
//    public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
//        if(getLatLong() != null && getBitmap() != null) {
//            long mapSize = MercatorProjection.getMapSize(zoomLevel, this.displayModel.getTileSize());
//            int pixelX = (int) MercatorProjection.longitudeToPixelX(getLatLong().longitude, mapSize);
//            int pixelY = (int) MercatorProjection.latitudeToPixelY(getLatLong().latitude, mapSize);
//            int halfBitmapWidth = this.getBitmap().getWidth() / 2;
//            int halfBitmapHeight = this.getBitmap().getHeight() / 2;
//            int left = (int) (pixelX - topLeftPoint.x - (double) halfBitmapWidth + (double) getHorizontalOffset());
//            int top = (int) (pixelY - topLeftPoint.y - (double) halfBitmapHeight + (double) getVerticalOffset());
//            int right = left + this.getBitmap().getWidth();
//            int bottom = top + this.getBitmap().getHeight();
//            Rectangle bitmapRectangle = new Rectangle((double) left, (double) top, (double) right, (double) bottom);
//            Rectangle canvasRectangle = new Rectangle(0.0D, 0.0D, (double) canvas.getWidth(), (double) canvas.getHeight());
////            if (!intersect(canvas, left, top, right, bottom)) {
////                return;
////            }
////            if (canvasRectangle.intersects(bitmapRectangle)) {
////                canvas.drawBitmap(getBitmap(), left, top);
////            }
//
////        Rect drawableBounds = getBitmap().copyBounds();
////        int left = pixelX + drawableBounds.left;
////        int top = pixelY + drawableBounds.top;
////        int right = pixelX + drawableBounds.right;
////        int bottom = pixelY + drawableBounds.bottom;
//
////        if (!intersect(canvas, left, top, right, bottom)) {
////            return false;
////        }
////
////        getBitmap().setBounds(left, top, right, bottom);
////        getBitmap().draw(canvas);
////        getBitmap().setBounds(drawableBounds);
//
//
//            //canvas.drawText(text, pixelX + offsetX, pixelY + offsetY, paint);
//            canvas.drawText(text, left, top, paint);
//
//            return;
//        }
//    }
//
//    private static boolean intersect(Canvas canvas, float left, float top, float right, float bottom) {
//        return right >= 0 && left <= canvas.getWidth() && bottom >= 0 && top <= canvas.getHeight();
//    }
//
//
//    public void setOffsetX(int offsetX) {
//        this.offsetX = offsetX;
//    }
//
//    public void setOffsetY(int offsetY) {
//        this.offsetY = offsetY;
//    }
//    public void setText(String s) {
//        this.text = s;
//    }
//    public void setPaing(Paint p) {
//        this.paint = p;
//    }
//
//}