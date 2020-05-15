package com.verocorp.soundsoulapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class SongPlayer extends AppCompatActivity {

    ImageView coverImagen;
    ImageButton likeButton;
    ImageButton informationButton;
    ImageButton previousButtonView;
    ImageButton playButtonView;
    ImageButton nextButtonView;
    SeekBar seekBarProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_song);

    coverImagen = findViewById(R.id.cover);
    likeButton = findViewById(R.id.like);
    informationButton = findViewById(R.id.information);
    previousButtonView = findViewById(R.id.previousButton);
    playButtonView = findViewById(R.id.playButton);
    nextButtonView = findViewById(R.id.nextButton);
    seekBarProgress = findViewById(R.id.seekBar);


    likeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast like =
                    Toast.makeText(getApplicationContext(),
                            "Guardada en Favoritas", Toast.LENGTH_SHORT);

            like.show();
        }
    });

    informationButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast information =
                    Toast.makeText(getApplicationContext(),
                            "Añadir información", Toast.LENGTH_SHORT);

            information.show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    });

    previousButtonView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast previous =
                    Toast.makeText(getApplicationContext(),
                            "Anterior", Toast.LENGTH_SHORT);

            previous.show();
        }
    });

    playButtonView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast play =
                    Toast.makeText(getApplicationContext(),
                            "Play", Toast.LENGTH_SHORT);

            play.show();
        }
    });

    nextButtonView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast next =
                    Toast.makeText(getApplicationContext(),
                            "Siguiente", Toast.LENGTH_SHORT);

            next.show();
        }
    });

    }

}
