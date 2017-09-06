package com.SitioWifi;

/**
 * Created by Gatsu on 10/18/2016.
 */
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Banner extends Activity {

    ArrayList<String> banners;
    String bannerURL;
    ImageView banner;
    int count;
    TextView cronometro;
    Bitmap myBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.banner_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Inicio inicio  = new Inicio();
        banners = inicio.getBanners();
        bannerURL = "";

        if (banners.size() > 0) {
            bannerURL = banners.get(0);
            banners.remove(0);

        }
        else{
            banners = inicio.crearBanners();
            bannerURL = banners.get(0);
            banners.remove(0);
        }
        Maps map = new Maps();
        Log.d("BANNERl ", bannerURL);
        myBitMap = map.getBitmapFromURL(bannerURL);

        banner = (ImageView) findViewById(R.id.Banner);
        cronometro = (TextView) findViewById (R.id.tiempo);
        banner.post(new Runnable() {
            @Override
            public void run() {
                banner.setImageBitmap(myBitMap);
                count = 10;
                Timer T=new Timer();
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Banner.this.runOnUiThread(new Runnable() {
                            public void run() {
                                cronometro.setText(String.valueOf(count));
                                count--;
                                if (count < 0){
                                    cancel();
                                    finish();
                                }
                            }
                        });
                    }
                },0, 1000);
            }
        });



    }

    @Override
    public void onBackPressed(){

    }

}