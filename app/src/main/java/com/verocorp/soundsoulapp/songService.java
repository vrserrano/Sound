package com.verocorp.soundsoulapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class songService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle=false;
    private Random rand;



    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());



    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(){
        //reproduce la canción
        //consigue la canción
        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
        //consigue la canción a través del ID
        long currSong = playSong.getID();
        //consigue la ruta donde se aloja la canción
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        player.reset();

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("SONG SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

    }


    //Interacción entre songService y activity
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    //Reproducción aleatoria
    public void setShuffle(){
        if(shuffle)  shuffle=false;
        else shuffle=true;
    }

    private String songTitle=";";
    private static final int NOTIFY_ID=1;


    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    //También se accedera desde la clase activity
    class MusicBinder extends Binder {
        songService getService() {

            return songService.this;
        }
    }

    @Nullable
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
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

     @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
     @Override
     public void onPrepared(MediaPlayer mp) {
            //Inicio reproducción
            player.start();
         Intent notIntent = new Intent(this, MainActivity.class);
         notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                 notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

         Notification.Builder builder = new Notification.Builder(this);

         builder.setContentIntent(pendInt)
                 .setSmallIcon(R.drawable.play)
                 .setTicker(songTitle)
                 .setOngoing(true)
                 .setContentTitle("Playing")
                 .setContentText(songTitle);
         Notification not = builder.build();

         startForeground(NOTIFY_ID, not);
        }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    //Funciones para controlar la reproducción
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    //Función anterior
    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    //Siguiente canción
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.verocorp.soundsoulapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon_1)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}

