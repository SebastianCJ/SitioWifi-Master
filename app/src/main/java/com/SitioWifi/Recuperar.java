package com.SitioWifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gatsu on 9/23/2016.
 */
public class Recuperar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_recuperarcontra);

        Button btonRecuperar = (Button)findViewById(R.id.btn_recuperar);

        ImageView img= (ImageView) findViewById(R.id.imageLogo);
        img.setImageResource(R.drawable.logo);

        TextView loginTxt = (TextView) findViewById(R.id.cuentaText);
        TextView registroTxt = (TextView) findViewById(R.id.registrate);


        loginTxt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Login.class);
                startActivity(Intent);
            }
        });

        registroTxt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Login.class);
                startActivity(Intent);
            }
        });

        btonRecuperar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        });




    }
}

