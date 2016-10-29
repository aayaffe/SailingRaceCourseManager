package com.aayaffe.sailingracecoursemanager.Map_Layer;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

/**
 * Created by aayaffe on 02/10/2015.
 */
public class MapUtils {

    public static Bitmap createBoatIcon(Bitmap boat, Bitmap mark, int number, String color, Resources r) {
        Bitmap bitmap = null;
        try {

            bitmap = Bitmap.createBitmap(boat.getWidth(), boat.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);

            Drawable drawable1 = new BitmapDrawable(drawTextToBitmap(boat, String.valueOf(number), r.getDisplayMetrics().density));
            Drawable drawable2 = new BitmapDrawable(mark);

            drawable1.setBounds(0,0,boat.getWidth(),boat.getHeight());
            drawable2.setBounds(boat.getWidth() / 2, boat.getHeight()/2,60,60);
            drawable1.draw(c);
            drawable2.draw(c);



        } catch (Exception e) {
        }
        return bitmap;
    }

    public static Bitmap drawTextToBitmap(Bitmap b, String gText, float density) {


        android.graphics.Bitmap.Config bitmapConfig =
                b.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        b = b.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(b);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (14 * density));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (b.getWidth() - bounds.width())/2;
        int y = (b.getHeight() + bounds.height()/2-19);

        canvas.drawText(gText, x, y, paint);

        return b;
    }
    public static org.mapsforge.core.graphics.Bitmap addBoatNumber(org.mapsforge.core.graphics.Bitmap b, int num, Resources r){
        Bitmap ab = drawTextToBitmap(AndroidGraphicFactory.getBitmap(b),String.valueOf(num),r.getDisplayMetrics().density);
        return AndroidGraphicFactory.convertToBitmap(new BitmapDrawable(ab));
    }
}
