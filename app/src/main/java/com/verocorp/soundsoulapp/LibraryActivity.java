package com.verocorp.soundsoulapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {
    boolean mBound = false;
    SongService songService;
    ListView songView;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

         songView = findViewById(R.id.song_list);
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
        View closeActivity = null;
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), SongService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SongService.SongServiceBinder binder = (SongService.SongServiceBinder) service;
            songService = binder.getService();
            mBound = true;
            SongAdapter songAdt = new SongAdapter(LibraryActivity.this, songService.getSongs());
            songView.setAdapter(songAdt);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void songPicked(View view) {
        songService.setSong(Integer.parseInt(view.getTag().toString()));
        songService.playSong();
        finish();
    }
}
