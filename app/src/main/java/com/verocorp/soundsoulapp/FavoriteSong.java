package com.verocorp.soundsoulapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;


public class FavoriteSong extends AppCompatActivity {
    boolean mBound = false;
    SongService songService;
    SongAdapter songAdt;
    ListView favoritesSongView;
    private DataBaseSoundSoulApp mDbHelper;
    private static final int DELETE_ID = Menu.FIRST + 1;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_song);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        favoritesSongView = findViewById(R.id.favorites_song_list);
        mDbHelper = new DataBaseSoundSoulApp(this);
        mDbHelper.open();
        songAdt = new SongAdapter(FavoriteSong.this, mDbHelper.fetchAllSongs());
        favoritesSongView.setAdapter(songAdt);
        registerForContextMenu(favoritesSongView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void Player() {
        Intent intent = new Intent(getApplicationContext(), SongPlayer.class);
        startActivity(intent);
    }
    private void closeActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteSong(info.targetView.getTag().toString());
            finish();
            startActivity(getIntent());
            return true;
        }
        return super.onContextItemSelected(item);
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
        mDbHelper.close();
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SongService.SongServiceBinder binder = (SongService.SongServiceBinder) service;
            songService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void songPicked(View view) {
        songService.setSong(view.getTag().toString());
        songService.playSong();
        finish();
    }
}