package com.SitioWifi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Gatsu on 10/19/2016.
 */
public class Encuestas extends Activity {
    RadioGroup respuesta1, respuesta2, respuesta3, respuesta4, respuesta5;
    ProgressDialog pDialog;
    ArrayList<TextView> PreguntasViews = new ArrayList<>();
    ArrayList<RadioGroup> Respuestas = new ArrayList<>();
    private static ArrayList<String> preguntasList = new ArrayList<>();
    private static ArrayList<String> idElementosList = new ArrayList<>();
    private static ArrayList<String> respuestasList = new ArrayList<>();
    int suma=0,suma2=0,numPreguntas;
    ArrayList<String> encuestas;
    String idEncuesta;
    public SharedPreferences datosPersistentes;
    String idPublicidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.encuesta_layout);

        Inicio inicio  = new Inicio();
        encuestas = inicio.getEncuestas();
        idEncuesta = "";

        if (encuestas.size() > 0) {
            idEncuesta = encuestas.get(0);
            encuestas.remove(0);
        }
        else{
            encuestas = inicio.crearEncuestas();
            idEncuesta = encuestas.get(0);
            encuestas.remove(0);
        }

        TextView titulo = (TextView) findViewById(R.id.titulo_encuesta);
        TextView pregunta1 = (TextView) findViewById(R.id.pregunta1);
        TextView pregunta2 = (TextView) findViewById(R.id.pregunta2);
        TextView pregunta3 = (TextView) findViewById(R.id.pregunta3);
        TextView pregunta4 = (TextView) findViewById(R.id.pregunta4);
        TextView pregunta5 = (TextView) findViewById(R.id.pregunta5);

        PreguntasViews.add(pregunta1);
        PreguntasViews.add(pregunta2);
        PreguntasViews.add(pregunta3);
        PreguntasViews.add(pregunta4);
        PreguntasViews.add(pregunta5);

        respuesta1 = (RadioGroup) findViewById(R.id.radioGroup1);
        respuesta2 = (RadioGroup) findViewById(R.id.radioGroup2);
        respuesta3 = (RadioGroup) findViewById(R.id.radioGroup3);
        respuesta4 = (RadioGroup) findViewById(R.id.radioGroup4);
        respuesta5 = (RadioGroup) findViewById(R.id.radioGroup5);

        Respuestas.add(respuesta1);
        Respuestas.add(respuesta2);
        Respuestas.add(respuesta3);
        Respuestas.add(respuesta4);
        Respuestas.add(respuesta5);

        preguntasList = crearPreguntas(idEncuesta);
        numPreguntas = preguntasList.size();

        for (int i =0; i < numPreguntas;i++ ){
            TextView pregunta = PreguntasViews.get(i);
            pregunta.setVisibility(View.VISIBLE);
            pregunta.setText(preguntasList.get(i));
            Respuestas.get(i).setVisibility(View.VISIBLE);
        }

        String tituloTxt = "Favor de Responder la Siguiente Encuesta";

        titulo.setText(tituloTxt);
        boolean a = respuesta1.getVisibility() == View.VISIBLE;
        boolean b = respuesta2.getVisibility() == View.VISIBLE;
        boolean c = respuesta3.getVisibility() == View.VISIBLE;
        boolean d = respuesta4.getVisibility() == View.VISIBLE;
        boolean e = respuesta5.getVisibility() == View.VISIBLE;

        if (a){ suma++;}
        if (b){ suma++;}
        if (c){ suma++;}
        if (d){ suma++;}
        if (e){ suma++;}

        Button dialogButton = (Button) findViewById(R.id.btn_regresarEncuesta);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suma2=0;

                boolean a1 = respuesta1.getCheckedRadioButtonId() != -1;
                boolean b1 = respuesta2.getCheckedRadioButtonId() != -1;
                boolean c1 = respuesta3.getCheckedRadioButtonId() != -1;
                boolean d1 = respuesta4.getCheckedRadioButtonId() != -1;
                boolean e1 = respuesta5.getCheckedRadioButtonId() != -1;

                if (a1){ suma2++;}
                if (b1){ suma2++;}
                if (c1){ suma2++;}
                if (d1){ suma2++;}
                if (e1){ suma2++;}

                if (suma == suma2){
                    //Send data to database
                    new InsertDatabaseTask().execute();

                }
                else{
                    Toast.makeText(Encuestas.this,"No puede haber respuestas vacias",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed(){

    }

    public ArrayList<String> crearPreguntas(String idEncuesta){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
            respuesta = conexion.conexionServidor(serverUrl, "action=preguntas&idEncuesta=" + idEncuesta);
            System.out.println("action=preguntas&idEncuesta=" + idEncuesta);
            if (respuesta.getString("success").equals("OK")) {

                JSONArray preguntas = respuesta.getJSONArray("preguntas");

                int i = 0;
                preguntasList = new ArrayList<>();
                idPublicidad = preguntas.getJSONObject(0).getString("idpublicidad");
                while (i < preguntas.length()) {
                    JSONObject pregunta = preguntas.getJSONObject(i);
                    preguntasList.add(pregunta.getString("pregunta"));
                    idElementosList.add(pregunta.getString("idelemento"));
                    i++;
                }
                return preguntasList;

            } else {
                Toast.makeText(Encuestas.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private class InsertDatabaseTask extends AsyncTask<URL, String, Integer> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Encuestas.this);
            // Set progressbar message
            pDialog.setMessage("Enviando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            // Show progressbar
            pDialog.show();
            for (int i = 0; i < numPreguntas ;i++) {
                int selectedId = Respuestas.get(i).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                respuestasList.add(radioButton.getText().toString());
            }
        }

        protected Integer doInBackground(URL... urls) {
            //String idusr = datosPersistentes.getString("idusrS1t10w1f1","");
            String idusr = "25";
            for (int i = 0; i < preguntasList.size();i++) {
                String idElemento = idElementosList.get(i);
                String respuesta = respuestasList.get(i);
                guardarRespuestas(idPublicidad,idElemento,respuesta,idusr);
            }
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            finish();
        }
    }

    private void guardarRespuestas(String idPublicidad,String idElemento, String valor, String idusr){
        JSONData conexion = new JSONData();
        JSONObject respuesta;
        try {
            String serverUrl = "http://distro.mx/SitioWifi/s1t10w1f1.php";
            respuesta = conexion.conexionServidor(serverUrl, "action=guardarRespuestas&idpublicidad=" + idPublicidad +"&idelemento="+ idElemento + "&valor=" + valor + "&idusr=" + idusr);
            Log.d("Url: ","action=guardarRespuestas&idpublicidad="+ idPublicidad +"&idelemento="+ idElemento + "&valor=" + valor + "&idusr=" + idusr);
            if (respuesta.getString("success").equals("OK")) {

            }else{
                Toast.makeText(Encuestas.this, respuesta.getString("success"), Toast.LENGTH_LONG).show();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

