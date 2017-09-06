package com.SitioWifi;

/**
 * Created by Gatsu on 10/18/2016.
 */
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import java.util.ArrayList;

public class Videos extends Activity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;
    ArrayList<String> videos;
    String VideoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_video);
        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.videoView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Inicio inicio  = new Inicio();
        videos = inicio.getVideos();
        VideoURL = "";

        if (videos.size() > 0) {
            VideoURL = videos.get(0);
            videos.remove(0);
            // Execute StreamVideo AsyncTask
        }
        else{
            videos = inicio.crearVideos();
            VideoURL = videos.get(0);
            videos.remove(0);
        }
        // Create a progressbar
        pDialog = new ProgressDialog(Videos.this);
        // Set progressbar message
        pDialog.setMessage("Cargando...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        try {

            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }

        });


    }

    @Override
    public void onBackPressed(){

    }

}