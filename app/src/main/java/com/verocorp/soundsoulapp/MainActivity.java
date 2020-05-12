package com.verocorp.soundsoulapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.widget.ListView;
import android.os.Bundle;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;
import com.verocorp.soundsoulapp.songService.MusicBinder;

import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private ArrayList<Song> songList;
    private ListView songView;
    private songService songSrv;
    private Intent playIntent;
    private boolean musicBound=false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } //Explains its use and why we need contacts

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return;
        }
        songView = findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        getSongList();

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, songService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    // connection to the service
    ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            songService.MusicBinder binder = (songService.MusicBinder)service;
            //get service
            songSrv = binder.getService();
            //pass list
            songSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList() {
        //recovers information from the song
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //columns with song information
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            //This conditional adds songs to the list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        songAdapter songAdt = new songAdapter(this, songList);
        songView.setAdapter(songAdt);

    }
        public void songPicked(View view){
            songSrv.setSong(Integer.parseInt(view.getTag().toString()));
            songSrv.playSong();

    }

        @Override
         public boolean onOptionsItemSelected(MenuItem item) {
                //menu item selected
            switch (item.getItemId()) {
                case R.id.action_shuffle:
                    songSrv.setShuffle();
                    break;
                case R.id.action_end:
                    stopService(playIntent);
                    songSrv=null;
                    System.exit(0);
                    break;
            }
            return super.onOptionsItemSelected(item);
    }
         @Override
           protected void onDestroy() {
                 stopService(playIntent);
                 songSrv=null;
                 super.onDestroy();
    }
}
