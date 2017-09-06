package com.SitioWifi;

/**
 * Created by Toker on 19/09/2016.
 */

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



public class DrawSurfaceView extends View implements View.OnTouchListener {
    private Point me = new Point(31.861831, -116.595716,"me");
    Paint mPaint = new Paint();
    private double OFFSET = 0d;
    private double screenWidth, screenHeight = 0d;
    private Bitmap[] mSpots, mBlips;
    private Bitmap mRadar;
    public SharedPreferences datosPersistentes;
    Maps Maps = new Maps();

    public DrawSurfaceView(Context c, Paint paint) {
        super(c);
    }

    public DrawSurfaceView(Context context, AttributeSet set) {
        super(context, set);
       // ArrayList<Point> props = crearMarcadores();
        ArrayList<Point> props = Maps.returnList();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(50);
        mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
        mPaint.setAntiAlias(true);
        mRadar = BitmapFactory.decodeResource(context.getResources(), R.mipmap.radar);

        mSpots = new Bitmap[props.size()];
        for (int i = 0; i < mSpots.length; i++)
            mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.marker);

        mBlips = new Bitmap[props.size()];
        for (int i = 0; i < mBlips.length; i++)
            mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sitioradar);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
        screenWidth = (double) w;
        screenHeight = (double) h;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        ArrayList<Point> props= com.SitioWifi.Maps.returnList();

        datosPersistentes = getContext().getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        double latitud = Maps.getDouble(datosPersistentes,"latitudS1t10w1f1",0);
        double longitud = Maps.getDouble(datosPersistentes,"longitudS1t10w1f1",0);

        me.setLatitude(latitud);
        me.setLongitude(longitud);

//        while (props.size() < 1)
//        {
//            props=crearMarcadores();
//        }

        canvas.drawBitmap(mRadar, 20, 20, mPaint);

        int radarCentreX = mRadar.getWidth() / 2;
        int radarCentreY = mRadar.getHeight() / 2;

        for (int i = 0; i < mBlips.length; i++) {
            Bitmap blip = mBlips[i];
            Bitmap spot = mSpots[i];
            Point u = props.get(i);

            double dist = distInMetres(me, u);

            if (blip == null || spot == null)
                continue;

            if(dist > 70)
                dist = 70; //we have set points very far away for demonstration

            double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
            double xPos, yPos;

            if(angle < 0)
                angle = (angle+360)%360;

            xPos = Math.sin(Math.toRadians(angle)) * dist;
            yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));

            if (angle > 90 && angle < 270)
                yPos *= -1;

            double posInPx = angle * (screenWidth / 90d);

            int blipCentreX = blip.getWidth() / 2;
            int blipCentreY = blip.getHeight() / 2;

            xPos = xPos - blipCentreX;
            yPos = yPos + blipCentreY;
            canvas.drawBitmap(blip, (radarCentreX + (int) xPos), (radarCentreY - (int) yPos), mPaint); //radar blip

            //reuse xPos
            int spotCentreX = spot.getWidth() / 2;
            int spotCentreY = spot.getHeight() / 2;
            xPos = posInPx - spotCentreX;

            if (angle <= 45)
                u.x = (float) ((screenWidth / 2) + xPos);

            else if (angle >= 315)
                u.x = (float) ((screenWidth / 2) - ((screenWidth*4) - xPos));

            else
                u.x = (float) (float)(screenWidth*9); //somewhere off the screen

            u.y = (float)screenHeight/2 + spotCentreY;
            canvas.drawBitmap(spot, u.x, u.y, mPaint); //camera spot
            //canvas.drawText(u.description, u.x, u.y, mPaint); //text
        }
    }


    public void setOffset(float offset) {
        this.OFFSET = offset;
    }

    public void setMyLocation(double latitude, double longitude) {
       // Point me = myLocation();
        me.latitude = latitude;
        me.longitude = longitude;
    }

    protected double distInMetres(Point me, Point u) {

        double lat1 = me.latitude;
        double lng1 = me.longitude;

        double lat2 = u.latitude;
        double lng2 = u.longitude;

        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist * 1000;
    }

    protected static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double longDiff = Math.toRadians(lon2 - lon1);
        double la1 = Math.toRadians(lat1);
        double la2 = Math.toRadians(lat2);
        double y = Math.sin(longDiff) * Math.cos(la2);
        double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1) * Math.cos(la2) * Math.cos(longDiff);

        double result = Math.toDegrees(Math.atan2(y, x));
        return (result+360.0d)%360.0d;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("Touch coordinates : ",String.valueOf(event.getX()) + "  " + String.valueOf(event.getY()));

        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Log.d("Touch coordinates : ",String.valueOf(event.getX()) + "  " + String.valueOf(event.getY()));
        return false;
    }


}
