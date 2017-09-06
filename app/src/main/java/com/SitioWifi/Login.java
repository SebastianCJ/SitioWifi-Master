package com.SitioWifi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


public class Login extends AppCompatActivity {

    private String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
    private Button btnIniciarSesion;
    private EditText txtUsuario;
    private EditText txtPass;
    public SharedPreferences datosPersistentes;
    //    private String ssid="DistroWIFI";
//    private String key="d1str0stud10";
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d("Success", "Login");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("Login Response: ", response.toString());

                                        // Application code
                                        try {
                                            String id = object.getString("id");
                                            String correo = object.getString("email");
                                            conectarFB(id,correo);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Login.this, "Inicio de Sesion Cancelado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

        Button btonFB = (Button)findViewById(R.id.btn_fb);

        btonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile","email"));
            }

        });

        TextView contrasenaTxt = (TextView) findViewById(R.id.contrasenaText);

        TextView registroTxt = (TextView) findViewById(R.id.registrate);


        registroTxt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(Intent);
            }
        });

        contrasenaTxt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Recuperar.class);
                startActivity(Intent);
            }
        });


        MultiDex.install(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //vemos si tenemos guardado nombre de usuario y/o contraseña, en caso de que esten guardados, los mostramos
        datosPersistentes = getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        String usuarioguardado = datosPersistentes.getString("usrS1t10w1f1","");
        String passguardado = datosPersistentes.getString("passS1t10w1f1","");

        btnIniciarSesion = (Button)findViewById(R.id.btn_login);
        txtUsuario = (EditText) findViewById(R.id.emailTxt);
        txtPass = (EditText) findViewById(R.id.passwordTxt);

        //si ya hay datos guardados, se muestran en pantalla
        if (usuarioguardado.length() > 0 && passguardado.length()> 0 ) {
            txtUsuario.setText(usuarioguardado);
            txtPass.setText(passguardado);
        }
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() { // hago clic en el botón

            @Override
            public void onClick(View v) {
                String enteredUsername = txtUsuario.getText().toString();
                String enteredPassword = txtPass.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(Login.this, "No pueden existir campos vacios", Toast.LENGTH_LONG).show();
                    return;
                }

                if (enteredUsername.length() <= 1 || enteredPassword.length() <= 1) {
                    Toast.makeText(Login.this, "La longitud debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                    return;
                }

                //autentificacion con el servidor remoto
                conectar();
            }
        });
        //se verifica que exista conexion a internet
        boolean running = true;
        int intentos = 0;
        boolean conectado = false;
        while(running && intentos<20) {

            try {
                ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    if (info.isConnected()) {
                        running=false;
                        conectado = true;
                    }

                }
                Thread.sleep(1000);
                intentos++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if(!conectado){
            if (!usuarioguardado.equals("") && !passguardado.equals("")) {
                //se entra a la aplicacion
                startActivity(new Intent(getApplicationContext(), Maps.class));
            }else{
                final Dialog dialog = new Dialog(Login.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.alert_accept);

                TextView titulo = (TextView) dialog.findViewById(R.id.txtTituloAlert);
                TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                titulo.setText("Error");
                contenido.setText("Se requiere de una conexion a internet para iniciar sesión en el dispositivo.");
                primerboton.setVisibility(View.GONE);
                segundoboton.setText("OK");
                tercerboton.setVisibility(View.GONE);

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void conectar(){
        String enteredUsername = txtUsuario.getText().toString();
        String enteredPassword = txtPass.getText().toString();
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=login&usuario=" + enteredUsername + "&pass=" + enteredPassword);
            Log.d("url: ","action=login&usuario=" + enteredUsername + "&pass=" + enteredPassword);
            if (respuesta.getString("success").equals("OK")) {

                //se guardan los datos de manera persistente.
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("usrS1t10w1f1", enteredUsername);
                editarDatosPersistentes.putString("passS1t10w1f1", enteredPassword);
                editarDatosPersistentes.putString("idusrS1t10w1f1", respuesta.getString("idusuario"));
                editarDatosPersistentes.putString("nombreS1t10w1f1",respuesta.getString("nombre"));
                editarDatosPersistentes.putString("ssidS1t10w1f1", respuesta.getString("ssid"));
                editarDatosPersistentes.putString("keyS1t10w1f1",respuesta.getString("key"));
                editarDatosPersistentes.putString("tiempoS1t10w1f1",respuesta.getString("tiempo"));
                System.out.println(respuesta.getString("key")+ "KEEEEEY");
                editarDatosPersistentes.apply();
                //se entra a la aplicacion
                conectarWifi();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), Maps.class));
                    }
                }, 2000);

            } else {
                Toast.makeText(Login.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }



    public void conectarWifi(){
        SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
        datosPersistentes = getSharedPreferences("S1t10w1f1", Context.MODE_PRIVATE);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        String ssid = datosPersistentes.getString("ssidS1t10w1f1", "");
        String key = datosPersistentes.getString("keyS1t10w1f1", "");
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        //remember id
        Integer foundNetworkID = findNetworkInExistingConfig(wifiManager,
                wifiConfig.SSID);
        if (foundNetworkID != null) {
            Log.i("Remover: ", "Removing old configuration for network " + wifiConfig.SSID);
            wifiManager.disableNetwork(foundNetworkID);
            wifiManager.removeNetwork(foundNetworkID);
            wifiManager.saveConfiguration();
        }
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        editarDatosPersistentes.putInt("netIDS1t10w1f1", netId);
        System.out.println("WIFIID LOGIN:" + netId);
        editarDatosPersistentes.apply();
    }

    public void conectarFB(String idfb,String correo){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=loginfb&idfb=" + idfb);
            Log.d("url: ","action=loginfb&idfb=" + idfb);
            if (respuesta.getString("success").equals("OK")) {

                //se guardan los datos de manera persistente.
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("usrS1t10w1f1", correo);
                editarDatosPersistentes.putString("idusrS1t10w1f1", respuesta.getString("idfb"));
                editarDatosPersistentes.putString("nombreS1t10w1f1",respuesta.getString("nombre"));
                editarDatosPersistentes.putString("ssidS1t10w1f1", respuesta.getString("ssid"));
                editarDatosPersistentes.putString("keyS1t10w1f1",respuesta.getString("key"));
                editarDatosPersistentes.putString("tiempoS1t10w1f1",respuesta.getString("tiempo"));
                editarDatosPersistentes.apply();
                //se entra a la aplicacion
                //conectarWifi();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), Maps.class));
                    }
                }, 2000);

            } else {
                Toast.makeText(Login.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Intent Intent = new Intent(getApplicationContext(), Inicio.class);
        startActivity(Intent);
    }

    private static Integer findNetworkInExistingConfig(WifiManager wifiManager,
                                                       String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(ssid)) {
                return existingConfig.networkId;
            }
        }
        return null;
    }

}
