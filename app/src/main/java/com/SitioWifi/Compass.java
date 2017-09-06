package com.SitioWifi;

/**
 * Created by Toker on 19/09/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Compass extends Activity implements View.OnTouchListener {

    private static final String TAG = "Compass";
    private static boolean DEBUG = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private DrawSurfaceView mDrawView;
    LocationManager locMgr;
    final String[] data ={"Editar Información","Cerrar Sesión"};
    private SharedPreferences datosPersistentes;

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (DEBUG)
                Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
            if (mDrawView != null) {
                mDrawView.setOffset(event.values[0]);
                mDrawView.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        datosPersistentes = getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_arcamera);
        }
        else{
            setContentView(R.layout.activity_camera);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }

        final ImageView openMenu = (ImageView) findViewById(R.id.btnDrawer);
        final ImageView closeMenu = (ImageView) findViewById(R.id.btnClose);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, data);


        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.drawer);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override @SuppressWarnings("Deprecation")
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id){
                drawer.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){

                });
                switch(pos){
                    case 1:
                        Intent editarInfo = new Intent(Compass.this, Registro.class);
                        startActivity(editarInfo);
                        break;
                    case 2:
                        final Dialog dialog = new Dialog(Compass.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.alert_accept);

                        TextView titulo = (TextView) dialog.findViewById(R.id.txtTituloAlert);
                        TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                        Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                        Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                        Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                        titulo.setText("¡Atención!");
                        contenido.setText("¿Estas seguro que deseas cerrar sesión?");
                        primerboton.setText("OK");
                        segundoboton.setVisibility(View.GONE);
                        tercerboton.setText("CANCELAR");


                        primerboton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //desconectarWifi();
                                Intent cerrarSesion = new Intent(Compass.this, Inicio.class);
                                startActivity(cerrarSesion);
                                finish();
                            }
                        });

                        tercerboton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        drawer.closeDrawer(navList);
                        break;

                }
                //drawer.closeDrawer(navList);
            }
        });

        TextView textView = new TextView(this);
        String nombre = datosPersistentes.getString("nombreS1t10w1f1","");
        textView.setText("Bienvenido " + nombre);
        textView.setTextSize(16);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (15*scale + 0.5f);
        textView.setPadding(dp,dp,0,dp);
        navList.addHeaderView(textView);

        openMenu.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
                openMenu.setVisibility(View.GONE);
                closeMenu.setVisibility(View.VISIBLE);
            }
        });

        closeMenu.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {;
                drawer.closeDrawer(navList);
                openMenu.setVisibility(View.VISIBLE);
                closeMenu.setVisibility(View.GONE);
            }
        });

        Button btonCamara = (Button)findViewById(R.id.AR_Camera);

        btonCamara.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                Intent Intent = new Intent(getApplicationContext(), Maps.class);
                startActivity(Intent);
            }

        });

        mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);
        mDrawView.setOnTouchListener(this);


        locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE); // <2>
        LocationProvider high = locMgr.getProvider(locMgr.getBestProvider(
                LocationUtils.createFineCriteria(), true));

        // using high accuracy provider... to listen for updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locMgr.requestLocationUpdates(high.getName(), 0, 0f,
                new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // do something here to save this new location
                        //Log.d(TAG, "Location Changed");
                        mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
                        mDrawView.invalidate();
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    public void onProviderEnabled(String s) {
                        // try switching to a different provider
                    }

                    public void onProviderDisabled(String s) {
                        // try switching to a different provider
                    }
                });

    }

    public void desconectarWifi(){
        int wifiId = datosPersistentes.getInt("netIDS1t10w1f1",0);
        System.out.println("WIFIID MAPS:" + wifiId);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.removeNetwork(wifiId);
        wifi.disableNetwork(wifiId);
        wifi.disconnect();
        wifi.saveConfiguration();
    }


    @Override
    protected void onResume() {
        if (DEBUG)
            Log.d(TAG, "onResume");
        super.onResume();

        mSensorManager.registerListener(mListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        if (DEBUG)
            Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Log.d("Touch coordinates : ",String.valueOf(event.getX()) + "-" + String.valueOf(event.getY()));

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("Touch coordinates : ",String.valueOf(event.getX()) + "-" + String.valueOf(event.getY()));

        return true;
    }

}
