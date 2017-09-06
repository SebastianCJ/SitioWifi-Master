package com.SitioWifi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Gatsu on 9/20/2016.
 */
public class Registro extends AppCompatActivity{
    EditText nombreTxt;
    EditText emailTxt;
    EditText passwordTxt;
    Spinner fechaTxt;
    Spinner dropdown;
    Spinner spinnerMes;
    Spinner fechaTxt2;
    String fecha;
    String email;
    String birthday;
    String id;
    String genero;
    String name;
    String pass;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("Login Response: ", response.toString());

                                        // Application code
                                        try {
                                            email = object.getString("email");
                                            birthday = "0/0/0"; // 01/31/1980 format
                                            id = object.getString("id");
                                            name = object.getString("name");
                                            pass = " ";
                                            genero = object.getString("gender");

                                            if (genero.equals("male")){
                                                genero = "Hombre";
                                            }
                                            else{
                                                genero = "Mujer";
                                            }

                                            registrar(id,name,email,pass,birthday,genero);
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
                        // App code
                        Log.v("LoginActivity", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        Button btonFB = (Button)findViewById(R.id.btn_fbReg);

        btonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Registro.this, Arrays.asList("public_profile","email"));
            }

        });

        Button btnregistrar = (Button)findViewById(R.id.btn_registrar);

        TextView prefieresTxt = (TextView) findViewById(R.id.txtSeparator);
        TextView loginTxt = (TextView) findViewById(R.id.cuentaText);

        nombreTxt = (EditText) findViewById(R.id.nombreTxt);
        emailTxt = (EditText) findViewById(R.id.emailTxtR);
        passwordTxt = (EditText) findViewById(R.id.passwordTxtR);
        fechaTxt = (Spinner) findViewById(R.id.fechaTxt);
        spinnerMes = (Spinner) findViewById(R.id.spinnerMes);
        dropdown = (Spinner)findViewById(R.id.spinner1);
        fechaTxt2 = (Spinner) findViewById(R.id.fechaTxt2);

        final String[] generos = new String[]{"Género","Hombre","Mujer"};
        final String[] meses = new String[]{"Selecciona Mes:","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        final String[] dias = new String[]{"Dia:","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        final String[] dias2 = new String[]{"Dia:","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"};
        final String[] diasFeb = new String[]{"Dia:","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29"};



        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int numero = ((thisYear-10)-1930)+2;
        String[] years = new String[numero];
        years[0] = "Año:";
        int j = 1;
        for (int i = 1930; i <= thisYear-10; i++) {
            String year = String.valueOf(i);
            years[j] = year;
            j++;
        }
        int hidingItemIndex = 0;

        SpinnerAdapter dataAdapterGenero = new SpinnerAdapter(this, R.layout.spinner_item, generos, hidingItemIndex);
        SpinnerAdapter dataAdapterMes = new SpinnerAdapter(this, R.layout.spinner_itemmes, meses, hidingItemIndex);
        final SpinnerAdapter dataAdapterDia = new SpinnerAdapter(this, R.layout.spinner_itemmes, dias, hidingItemIndex);
        final SpinnerAdapter dataAdapterDia2 = new SpinnerAdapter(this, R.layout.spinner_itemmes, dias2, hidingItemIndex);
        final SpinnerAdapter dataAdapterDiaFeb = new SpinnerAdapter(this, R.layout.spinner_itemmes, diasFeb, hidingItemIndex);
        SpinnerAdapter dataAdapterAnio = new SpinnerAdapter(this, R.layout.spinner_itemmes, years, hidingItemIndex);

        dataAdapterGenero.setDropDownViewResource(R.layout.spinner_item);
        dataAdapterMes.setDropDownViewResource(R.layout.spinner_itemmes);
        dataAdapterDia.setDropDownViewResource(R.layout.spinner_itemmes);
        dataAdapterDia2.setDropDownViewResource(R.layout.spinner_itemmes);
        dataAdapterDiaFeb.setDropDownViewResource(R.layout.spinner_itemmes);
        dataAdapterAnio.setDropDownViewResource(R.layout.spinner_itemmes);

        dropdown.setAdapter(dataAdapterGenero);
        spinnerMes.setAdapter(dataAdapterMes);
        fechaTxt.setAdapter(dataAdapterDia);
        fechaTxt2.setAdapter(dataAdapterAnio);

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch(position){
                    case 1:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;

                    case 2:
                        fechaTxt.setAdapter(dataAdapterDiaFeb);
                        break;
                    case 3:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;

                    case 4:
                        fechaTxt.setAdapter(dataAdapterDia2);
                        break;
                    case 5:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;

                    case 6:
                        fechaTxt.setAdapter(dataAdapterDia2);
                        break;
                    case 7:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;

                    case 8:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;
                    case 9:
                        fechaTxt.setAdapter(dataAdapterDia2);
                        break;

                    case 10:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;
                    case 11:
                        fechaTxt.setAdapter(dataAdapterDia2);
                        break;

                    case 12:
                        fechaTxt.setAdapter(dataAdapterDia);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        loginTxt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Login.class);
                startActivity(Intent);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnregistrar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String correo = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                String nombre = nombreTxt.getText().toString();
                String mes = spinnerMes.getSelectedItem().toString();
                String dia = fechaTxt.getSelectedItem().toString();
                String anio = fechaTxt2.getSelectedItem().toString();

                Integer index = Arrays.asList(meses).indexOf(mes);
                String mesFinal = String.valueOf(index);

                fecha = anio + "/" + mesFinal + "/" + dia;
                String genero = dropdown.getSelectedItem().toString();


                if (correo.equals("") || password.equals("") || nombre.equals("") || anio.equals("") || genero.equals("")|| mes.equals("") || dia.equals("") ) {
                    Toast.makeText(Registro.this, "No pueden existir campos vacios", Toast.LENGTH_LONG).show();
                    return;
                }

                if (correo.length() <= 1 || password.length() <= 1 || nombre.length() <= 1 || fecha.length() <= 1) {
                    Toast.makeText(Registro.this, "La longitud debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(genero.length() < 1){
                    Toast.makeText(Registro.this,"Selecciona tu género", Toast.LENGTH_LONG).show();
                    return;
                }


                registrar("0",nombre,correo,password,fecha,genero);
            }
        });

        ImageView img= (ImageView) findViewById(R.id.imageLogo);
        img.setImageResource(R.drawable.logo);
    }

    @Override
    public void onBackPressed() {
        Intent Intent = new Intent(getApplicationContext(), Inicio.class);
        startActivity(Intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void registrar(String idfb,String enteredName, String enteredUsername,String enteredPassword,String fechaNacimiento,String genero){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
            respuesta = conexion.conexionServidor(serverUrl, "action=registro&idfb=" + idfb +"&nombre="+ enteredName + "&usuario=" + enteredUsername + "&pass=" + enteredPassword + "&fechaNacimiento=" + fechaNacimiento + "&genero=" + genero);
            Log.d("Url: ","action=registro&idfb="+ idfb +"&nombre="+ enteredName + "&usuario=" + enteredUsername + "&pass=" + enteredPassword + "&fechaNacimiento=" + fechaNacimiento + "&genero=" + genero);
            if (respuesta.getString("existe").equals("SI")){
                Toast.makeText(Registro.this,"Tu Cuenta De Facebook Ya Esta Registrada",Toast.LENGTH_LONG).show();
            }
            else{
                if (respuesta.getString("success").equals("OK")) {

                    startActivity(new Intent(getApplicationContext(), Login.class));
                    Toast.makeText(Registro.this,"Registro Exitoso",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Registro.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
