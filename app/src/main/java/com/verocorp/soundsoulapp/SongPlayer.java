package com.verocorp.soundsoulapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SongPlayer extends AppCompatActivity {
    boolean mBound = false;
    SongService songService;
    Handler audioProgressUpdateHandler = null;
    SeekBar backgroundAudioProgress;
    ImageView coverImage;
    ImageButton likeButton;
    ImageButton libraryButton;
    ImageButton previousButtonView;
    ImageButton playButtonView;
    ImageButton nextButtonView;
    TextView currentTimeView;
    TextView totalTimeView;
    TextView songTitle;
    TextView songArtist;
    TextView songAlbum;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_song);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        coverImage = findViewById(R.id.cover);
        likeButton = findViewById(R.id.like);
        libraryButton = findViewById(R.id.library);
        previousButtonView = findViewById(R.id.previousButton);
        playButtonView = findViewById(R.id.playButton);
        nextButtonView = findViewById(R.id.nextButton);
        backgroundAudioProgress = findViewById(R.id.audioSeekbar);
        currentTimeView = findViewById(R.id.currentTime);
        totalTimeView = findViewById(R.id.totalTime);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        songAlbum = findViewById(R.id.songAlbum);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                return;
            }
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent libraryIntent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(libraryIntent);
            }
        });

        previousButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songService.playPrev();
            }
        });

        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songService.pauseAudio();
            }
        });

        nextButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songService.playNext();
            }
        });

        backgroundAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    songService.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SongService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d("LOGGGG", "onDestroy");
        unbindService(connection);
        mBound = false;
        super.onDestroy();
    }

    void setSongInfo() {
        Song currentSong = songService.getSong();
        songTitle.setText(currentSong.getTitle());
        songTitle.setTypeface(null, Typeface.BOLD);
        songArtist.setText(currentSong.getArtist());
        songAlbum.setText(currentSong.getAlbum());
    }

    public static String formatTime(int milliseconds) {
        String finalTimerString = "";
        String secondsString;

        int minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    @SuppressLint("HandlerLeak")
    private void createAudioProgressbarUpdater() {
        if (audioProgressUpdateHandler == null) {
            audioProgressUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == songService.UPDATE_AUDIO_PROGRESS_BAR && mBound) {
                        if (songService.isPlaying()) {
                            playButtonView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                        } else {
                            playButtonView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        }
                        setSongInfo();
                        int currentTimeMillis = songService.getCurrentAudioPosition();
                        backgroundAudioProgress.setMax(songService.getTotalAudioDuration());
                        backgroundAudioProgress.setProgress(currentTimeMillis);
                        currentTimeView.setText(formatTime(currentTimeMillis));
                        totalTimeView.setText(formatTime(songService.getTotalAudioDuration()));
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(songService.getSongPath());
                        try {
                            byte[] art = mmr.getEmbeddedPicture();
                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inSampleSize = 1;
                            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length, opt);
                            coverImage.setImageBitmap(songImage);
                        } catch (Exception e) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                coverImage.setImageDrawable(getDrawable(R.drawable.music));
                            }
                        }
                    }
                }
            };
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SongService.SongServiceBinder binder = (SongService.SongServiceBinder) service;
            songService = binder.getService();
            createAudioProgressbarUpdater();
            songService.setAudioProgressUpdateHandler(audioProgressUpdateHandler);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
