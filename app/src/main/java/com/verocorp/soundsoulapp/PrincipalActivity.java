package com.verocorp.soundsoulapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class PrincipalActivity extends AppCompatActivity {

    Button songPlayer;
    Button librarySong;
    Button favoriteSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        songPlayer = findViewById(R.id.player);
        librarySong = findViewById(R.id.library);
        favoriteSong = findViewById(R.id.favorite);


        //Player button

        songPlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SongPlayer.class);
                startActivity(intent);
            }
        });

        //Button for library

        librarySong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //Favorite button

        favoriteSong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FavoriteSong.class);
                startActivity(intent);
            }
        });
    }
}
