package com.verocorp.soundsoulapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer audioPlayer;
    private Handler audioProgressUpdateHandler;
    final int UPDATE_AUDIO_PROGRESS_BAR = 1;
    private int songPos = 0;
    private ArrayList<Song> songs;
    private final SongServiceBinder songServiceBinder = new SongServiceBinder();
    private static final int NOTIFY_ID = 1;

    public void onCreate() {
        super.onCreate();
        songPos = 0;
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        audioPlayer = new MediaPlayer();
        audioPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioPlayer.setOnPreparedListener(this);
        audioPlayer.setOnCompletionListener(this);
        audioPlayer.setOnErrorListener(this);
        audioPlayer.setLooping(true);
        setList(getSongList());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (audioPlayer.getCurrentPosition() > 0) {
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
        Intent notIntent = new Intent(getApplicationContext(), SongPlayer.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setTicker(getSong().getArtist())
                .setOngoing(true)
                .setContentTitle("SoundSoulApp Playing")
                .setContentText(getSong().getTitle());
        Notification not = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(NOTIFY_ID, not);
    }

    void setAudioProgressUpdateHandler(Handler audioProgressUpdateHandler) {
        this.audioProgressUpdateHandler = audioProgressUpdateHandler;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        audioPlayer.reset();
        audioPlayer.release();
        return super.onUnbind(intent);
    }

    void pauseAudio() {
        if (audioPlayer != null) {
            if (isPlaying()) {
                audioPlayer.pause();
            } else if (!isPlaying() && getCurrentAudioPosition() > 0) {
                audioPlayer.start();
            } else {
                playSong();
            }
        } else {
            playSong();
        }
    }

    void setSong(int songIndex) {
        songPos = songIndex;
    }

    Song getSong() {
        return songs.get(songPos);
    }

    boolean isPlaying() {
        if (audioPlayer != null) {
            return audioPlayer.isPlaying();
        } else {
            return false;
        }
    }

    void playSong() {
        audioPlayer.reset();
        try {
            audioPlayer.setDataSource(getApplicationContext(), getSongUri());
            audioPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioPlayer.start();

        Thread updateAudioProgressThread = new Thread() {
            @SuppressWarnings("InfiniteLoopStatement")
            @Override
            public void run() {
                while (true) {
                    Message updateAudioProgressMsg = new Message();
                    updateAudioProgressMsg.what = UPDATE_AUDIO_PROGRESS_BAR;

                    if (audioProgressUpdateHandler != null)
                        audioProgressUpdateHandler.sendMessage(updateAudioProgressMsg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        updateAudioProgressThread.start();
    }

    public ArrayList<Song> getSongs() {
        if (songs == null) {
            setList(getSongList());
        }
        return songs;
    }

    void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    void playPrev() {
        songPos--;
        if (songPos < 0) songPos = songs.size() - 1;
        playSong();
    }

    void playNext() {
        songPos++;
        if (songPos >= songs.size()) songPos = 0;
        playSong();
    }

    void seekTo(int position) {
        if (audioPlayer != null) {
            audioPlayer.seekTo(position);
        }
    }

    int getCurrentAudioPosition() {
        int ret = 0;
        if (audioPlayer != null) {
            ret = audioPlayer.getCurrentPosition();
        }
        return ret;
    }

    int getTotalAudioDuration() {
        int ret = 0;
        if (audioPlayer != null) {
            ret = audioPlayer.getDuration();
        }
        return ret;
    }

    String getSongPath() {
        Song playSong = songs.get(songPos);
        return playSong.getPath();
    }

    private Uri getSongUri() {
        Song playSong = songs.get(songPos);
        long currSong = playSong.getID();
        return ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public class SongServiceBinder extends Binder {
        SongService getService() {
            return SongService.this;
        }

        @Nullable
        @Override
        public String getInterfaceDescriptor() {
            return null;
        }

        @Override
        public boolean pingBinder() {
            return false;
        }

        @Override
        public boolean isBinderAlive() {
            return false;
        }

        @Nullable
        @Override
        public IInterface queryLocalInterface(@NonNull String descriptor) {
            return null;
        }

        @Override
        public void dump(@NonNull FileDescriptor fd, @Nullable String[] args) {

        }

        @Override
        public void dumpAsync(@NonNull FileDescriptor fd, @Nullable String[] args) {

        }

        @Override
        public void linkToDeath(@NonNull DeathRecipient recipient, int flags) {

        }

        @Override
        public boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags) {
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return songServiceBinder;
    }

    public ArrayList<Song> getSongList() {
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisPath = musicCursor.getString(pathColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisPath));
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        return songList;
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
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}