package com.verocorp.soundsoulapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class FavoriteSong extends AppCompatActivity {
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_song);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();

        switch (item.getItemId()) {
            case R.id.songPlayerActivity:
                Player();
                return true;
            case R.id.libraryActivity:
                MainActivity();
                return true;
            case R.id.favoritesActivity:
                FavoriteSong();
                return true;
            case R.id.closeApp:
                closeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void MainActivity() {
        Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
        startActivity(intent);
    }

    private void FavoriteSong() {
        Intent intent = new Intent(getApplicationContext(), FavoriteSong.class);
        startActivity(intent);
    }

    private void Player() {
        Intent intent = new Intent(getApplicationContext(), SongPlayer.class);
        startActivity(intent);
    }

    private void closeActivity() {
      this.finishActivity();
    }

    private void finishActivity() {
    }


}

