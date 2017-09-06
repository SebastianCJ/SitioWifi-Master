package com.SitioWifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Inicio extends AppCompatActivity {
    private String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
    private static ArrayList<String> videosList = new ArrayList<>();
    private static ArrayList<String> encuestasList = new ArrayList<>();
    private static ArrayList<String> bannersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_inicio);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        crearVideos();
        crearEncuestas();
        crearBanners();

        Button btonRegistro = (Button)findViewById(R.id.Registrarse);
        Button btonLogin = (Button)findViewById(R.id.IniciarSesionH);

        ImageView img= (ImageView) findViewById(R.id.imageLogo);
        img.setImageResource(R.drawable.logo);

        btonRegistro.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(Intent);
            }
        });

        btonLogin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), Login.class);
                startActivity(loginIntent);


            }

        });


    }
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private final static int INTERVAL = 1000 * 60 * 3; //30 minutes
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            Random rand = new Random();
            Intent myIntent;
            int n = rand.nextInt(30);
            if (n < 10) {
                myIntent = new Intent(Inicio.this, Videos.class);
                startActivity(myIntent);
            }
            if(n >= 10 && n <= 20) {
                myIntent = new Intent(Inicio.this, Encuestas.class);
                startActivity(myIntent);
            }
            if(n > 20){
                myIntent = new Intent(Inicio.this, Banner.class);
                startActivity(myIntent);
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    public void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

    public ArrayList<String> crearVideos(){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=videos");
            if (respuesta.getString("success").equals("OK")) {

                JSONArray videos = respuesta.getJSONArray("videos");

                int i = 0;
                videosList = new ArrayList<>();

                while (i < videos.length()) {
                    JSONObject video = videos.getJSONObject(i);
                    videosList.add(video.getString("url"));
                    i++;
                }
                Collections.shuffle(videosList);
                return videosList;

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> crearEncuestas(){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=encuestas");
            if (respuesta.getString("success").equals("OK")) {

                JSONArray videos = respuesta.getJSONArray("encuestas");

                int i = 0;
                encuestasList = new ArrayList<>();

                while (i < videos.length()) {
                    JSONObject video = videos.getJSONObject(i);
                    encuestasList.add(video.getString("idEncuesta"));
                    i++;
                }
                Collections.shuffle(encuestasList);
                return encuestasList;

            } else {
                Toast.makeText(Inicio.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> crearBanners(){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=banners");
            if (respuesta.getString("success").equals("OK")) {

                JSONArray banners = respuesta.getJSONArray("banners");

                int i = 0;
                bannersList = new ArrayList<>();

                while (i < banners.length()) {
                    JSONObject banner = banners.getJSONObject(i);
                    bannersList.add(banner.getString("url"));
                    i++;
                }
                Collections.shuffle(bannersList);
                return bannersList;

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getVideos(){
        return videosList;
    }

    public ArrayList<String> getEncuestas(){
        return encuestasList;
    }

    public ArrayList<String> getBanners(){
        return bannersList;
    }

    @Override
    public void onPause(){
        System.out.println("pausa");
        super.onPause();
        //startRepeatingTask();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("Destroy");

    }
}
