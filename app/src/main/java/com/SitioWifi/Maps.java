package com.SitioWifi;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Maps extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    boolean CRDS_CALL = false;
    final String[] data ={"Editar Información","Cerrar Sesión",};
    public SharedPreferences datosPersistentes;
    private static ArrayList<Point> props = new ArrayList<>();
    Map <String, Bitmap> imagenes = new HashMap<>();
    private ArrayList<Marker> arrayMarcadores = new ArrayList<>();
    RadioGroup respuesta1;
    RadioGroup respuesta2;
    RadioGroup respuesta3;
    double distancia;
    int num,INTERVAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        datosPersistentes = getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        String tiempo = datosPersistentes.getString("tiempoS1t10w1f1","");
        System.out.println(tiempo + "TIEMPO");
        num = Integer.valueOf(tiempo);
        final ImageView openMenu = (ImageView) findViewById(R.id.btnDrawer);
        final ImageView closeMenu = (ImageView) findViewById(R.id.btnClose);



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, data);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Ingresar Direccion");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public static final String TAG = "Tag: ";
            Marker marker;
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if( marker != null) {
                    marker.remove();
                }
                Log.i(TAG, "Place: " + place.getName());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                markerOptions.title("404");
                markerOptions.getTitle().equals("404");
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(place.getLatLng() ,16) );
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.drawer);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override @SuppressWarnings("Deprecation")
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
                drawer.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){

                });
                switch(pos){
                    case 1:
                        Intent editarInfo = new Intent(Maps.this, Registro.class);
                        startActivity(editarInfo);

                    break;
                    case 2:
                        final Dialog dialog = new Dialog(Maps.this);
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
                                Intent cerrarSesion = new Intent(Maps.this, Inicio.class);
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
            public void onClick(View v) {
                drawer.closeDrawer(navList);
                openMenu.setVisibility(View.VISIBLE);
                closeMenu.setVisibility(View.GONE);
            }
        });
        Button btonCamara = (Button)findViewById(R.id.AR_Camera);

        btonCamara.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v){
               Intent Intent = new Intent(getApplicationContext(), Compass.class);
               startActivity(Intent);
           }

    });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

    public static ArrayList<Point> returnList()
    {
        return(props);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override @SuppressWarnings("deprecation")
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setPadding(50,150,0,0);
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);
        datosPersistentes = getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        String tiempo = datosPersistentes.getString("tiempoS1t10w1f1","");
        num = Integer.valueOf(tiempo);
        INTERVAL = 1000 * num; //30 minutes
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRepeatingTask();

            }
        }, INTERVAL);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    Handler mHandler = new Handler();
    String publicidad = "Video";

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            Intent myIntent;

            if (publicidad.equals("Video")) {
                myIntent = new Intent(Maps.this, Videos.class);
                publicidad = "Encuesta";
                startActivity(myIntent);
            }
            else if(publicidad.equals("Encuesta")) {
                myIntent = new Intent(Maps.this, Encuestas.class);
                publicidad = "Banner";
                startActivity(myIntent);
            }
            else if(publicidad.equals("Banner")){
                myIntent = new Intent(Maps.this, Banner.class);
                publicidad = "Video";
                startActivity(myIntent);
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    @Override
    public void onConnected(Bundle bundle) {

        // Get last known recent location.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mLastLocation != null) {
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) ,4) );
            SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
            putDouble(editarDatosPersistentes,"latitudS1t10w1f1",mLastLocation.getLatitude());
            putDouble(editarDatosPersistentes,"longitudS1t10w1f1",mLastLocation.getLongitude());
            editarDatosPersistentes.apply();

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap resizeMapIcons(int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.marker);
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    private ArrayList<Point> crearMarcadores(){

        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
            Log.d("MARCADORES: ","action=marcadores&latitud="+mLastLocation.getLatitude()+"&longitud="+mLastLocation.getLongitude());
            respuesta = conexion.conexionServidor(serverUrl, "action=marcadores&latitud="+mLastLocation.getLatitude()+"&longitud="+mLastLocation.getLongitude());
            if (respuesta.getString("success").equals("OK")) {

                JSONArray marcadores = respuesta.getJSONArray("marcadores");

                int i = 0;
                props = new ArrayList<>();
                imagenes.clear();
                while (i < marcadores.length()) {
                    JSONObject marcador = marcadores.getJSONObject(i);

                    LatLng latLng = new LatLng(marcador.getDouble("latitud"), marcador.getDouble("longitud"));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(marcador.getString("nombre"));
                    markerOptions.snippet(marcador.getString("detalle"));

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                    Marker marker = mMap.addMarker(markerOptions);
                    String remotePath = "http://distro.mx/SitioWifi/imagenes/" + marcador.getString("imagen");
                    Bitmap myBitMap = getBitmapFromURL(remotePath);

                    arrayMarcadores.add(marker);
                    imagenes.put(marcador.getString("nombre"), myBitMap);

                    props.add(new Point(marcador.getDouble("latitud"), marcador.getDouble("longitud"), marcador.getString("nombre")));
                    i++;

                }
                return props;

            } else {
                Toast.makeText(Maps.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void borrarMarcadores(){
        for (int i=0;i < arrayMarcadores.size();i++){
            arrayMarcadores.get(i).remove();
        }
        arrayMarcadores.clear();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

            if (!marker.getTitle().equals("404")) {
                LatLng posicion = marker.getPosition();
                marker.hideInfoWindow();
                final Dialog dialog = new Dialog(Maps.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.activity_ubicacion);

                double distancia = SphericalUtil.computeDistanceBetween(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), posicion);

                TextView dist = (TextView) dialog.findViewById(R.id.distanciaMts);
                TextView nombreUbicacion = (TextView) dialog.findViewById(R.id.nombre_ubicacion);
                TextView detalle = (TextView) dialog.findViewById(R.id.detalle);
                ImageView imagenUbicacion = (ImageView) dialog.findViewById(R.id.imagenUbicacion);

                int distanciaInt = (int) Math.round(distancia);
                String distTxt = "Distancia: " + distanciaInt + " Mts";
                dist.setText(distTxt);
                nombreUbicacion.setText(marker.getTitle());
                detalle.setText(marker.getSnippet());
                Bitmap imagen = imagenes.get(marker.getTitle());
                imagenUbicacion.setImageBitmap(imagen);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_regresar);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        return true;
    }

    public void mostrarEncuesta(){
        final Dialog dialog = new Dialog(Maps.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.encuesta_layout);

        TextView titulo = (TextView) dialog.findViewById(R.id.titulo_encuesta);
        TextView pregunta1 = (TextView) dialog.findViewById(R.id.pregunta1);
        TextView pregunta2 = (TextView) dialog.findViewById(R.id.pregunta2);
        TextView pregunta3 = (TextView) dialog.findViewById(R.id.pregunta3);
        respuesta1 = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
        respuesta2 = (RadioGroup) dialog.findViewById(R.id.radioGroup2);
        respuesta3 = (RadioGroup) dialog.findViewById(R.id.radioGroup3);

        String tituloTxt = "Encuestas Uno";
        String pregunta1Txt = "Que te parece SitioWifi?";
        String pregunta2Txt ="Cuantas horas al dia utilizas SitioWifi?" ;
        String pregunta3Txt = "Tienes alguna sugerencia para mejorar SitioWifi? ";

        titulo.setText(tituloTxt);
        pregunta1.setText(pregunta1Txt);
        pregunta2.setText(pregunta2Txt);
        pregunta3.setText(pregunta3Txt);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_regresarEncuesta);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (respuesta1.getCheckedRadioButtonId() != -1 && respuesta2.getCheckedRadioButtonId() != -1 && respuesta3.getCheckedRadioButtonId() != -1){
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(Maps.this,"No puede haber respuestas vacias",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.show();
    }



    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = connection.getInputStream();
            options.inSampleSize = calculateInSampleSize(options, 50, 50);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input,null,options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LatLng getPosicion(){
        double latitud = getDouble(datosPersistentes,"latitudS1t10w1f1",0);
        double longitud = getDouble(datosPersistentes,"longitudS1t10w1f1",0);

        return new LatLng(latitud,longitud);
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        LatLng posicion = getPosicion();
        distancia = SphericalUtil.computeDistanceBetween(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), posicion);

        if (distancia >= 1000){
            SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
            borrarMarcadores();
            crearMarcadores();
            putDouble(editarDatosPersistentes,"latitudS1t10w1f1",mLastLocation.getLatitude());
            putDouble(editarDatosPersistentes,"longitudS1t10w1f1",mLastLocation.getLongitude());
            editarDatosPersistentes.apply();

        }

          LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(!CRDS_CALL){
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            borrarMarcadores();
            crearMarcadores();
            CRDS_CALL = true;
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        desconectarWifi();
//
//    }
}

