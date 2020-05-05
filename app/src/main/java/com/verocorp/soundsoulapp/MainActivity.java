package com.verocorp.soundsoulapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private ArrayList<Song> songList;
    private ListView songView;
    private songService songSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private SongController controller;
    private boolean paused=false, playbackPaused=false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        getSongList();
        setController();

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
    //connect to the service
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
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
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
            if(playbackPaused){
                setController();
                playbackPaused=false;
            }
            controller.show(0);
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

    @Override
    public void start() {
        songSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        songSrv.pausePlayer();
    }
    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public int getDuration() {
        if ( songSrv != null & amp; &amp;
        musicBound & amp;&amp;
        songSrv.isPng())
        return songSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(songSrv!=null &amp; &amp; musicBound &amp; &amp; songSrv.isPng())
        return songSrv.getPosn();
    }

    @Override
    public void seekTo(int pos) {
        songSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(songSrv!=null &amp;&amp; musicBound)
        return songSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController(){
        //set the controller up
        controller = new SongController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    //Siguiente canción
    private void playNext(){
        songSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //Anterior canción
    private void playPrev(){
        songSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }
}
