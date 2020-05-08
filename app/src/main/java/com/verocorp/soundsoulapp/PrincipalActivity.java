package com.verocorp.soundsoulapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class PrincipalActivity extends AppCompatActivity {

    Button SongPlayer;
    Button LibrarySong;
    Button FavoriteSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        SongPlayer = findViewById(R.id.reproductor);
        LibrarySong = findViewById(R.id.biblioteca);
        FavoriteSong = findViewById(R.id.favoritas);
    }


    /** Botón reproductor*/

    SongPlayer.setOnClickListener (new View.OnClickListener()

    {
        public void onClick (View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    });
}
