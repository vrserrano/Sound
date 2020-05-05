package com.verocorp.soundsoulapp;

import android.app.Service;
import java.util.ArrayList;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class SongsService extends Service {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    public void onCreate(){
        //create the service
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener((MediaPlayer.OnPreparedListener) this);
        player.setOnCompletionListener((MediaPlayer.OnCompletionListener) this);
        player.setOnErrorListener((MediaPlayer.OnErrorListener) this);
    }

    public void initMusicPlayer(){
        //set player properties
    }

    private final IBinder musicBind = new MusicBinder();

    public void setSong(int parseInt) {
    }

    public class SongService extends Service implements
            MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {


       @Override
            public IBinder onBind(Intent intent) {
                return musicBind;
        }
        @Override
        public boolean onUnbind(Intent intent){
            player.stop();
            player.release();
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {

        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
           //start playback
             mp.start();
            }
        public void setSong(int songIndex){
            songPosn=songIndex;
        }
        }
    }

        public void setList(ArrayList<Song> theSongs){
            songs=theSongs;
        }

        public class MusicBinder extends Binder {

            SongsService getService() {
            return SongsService.this;
        }


        public void playSong(){
            //play a song
            MediaPlayer player = null;
            player.reset();

              //get song
              Song playSong = songs.get(songPosn);
              //get id
              long currSong = playSong.getID();
              //set uri
              Uri trackUri = ContentUris.withAppendedId(
                      android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                     currSong);
                try{
                     player.setDataSource(getApplicationContext(), trackUri);
                }
                catch(Exception e){
                      Log.e("SONGs SERVICE", "Error setting data source", e);
                }

                 player.prepareAsync();
            }
        }
