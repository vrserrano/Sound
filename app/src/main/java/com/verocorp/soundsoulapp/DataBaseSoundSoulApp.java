package com.verocorp.soundsoulapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class DataBaseSoundSoulApp {
    private static final String KEY_ROWID = "_id";
    private static final String KEY_SONG_ID = "songID";
    private static final String KEY_SONG_TITLE = "songTitle";
    private static final String KEY_SONG_ARTIST = "songArtist";
    private static final String KEY_SONG_ALBUM = "songAlbum";
    private static final String KEY_SONG_PATH = "songPath";

    private static final String TAG = "DataBaseSoundSoulApp";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table songs (_id integer primary key autoincrement, "
                    + "songID INTEGER not null, songTitle text not null, songArtist text not null, songAlbum text not null, songPath text not null);";

    private static final String DATABASE_NAME = "favorites";
    private static final String DATABASE_TABLE = "songs";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS songs");
            onCreate(db);
        }
    }

    DataBaseSoundSoulApp(Context mCtx) {
        this.mCtx = mCtx;
    }

    void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    void close() {
        mDbHelper.close();
    }

    boolean addFavoriteSong(Song favoriteSong) {
        Cursor musicCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_SONG_ID, KEY_SONG_TITLE, KEY_SONG_ARTIST, KEY_SONG_ALBUM, KEY_SONG_PATH}, null, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                long thisId = musicCursor.getLong(1);
                if (thisId == favoriteSong.getID()) {
                    return false;
                }
            }
            while (musicCursor.moveToNext());

            musicCursor.close();
        }

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SONG_ID, favoriteSong.getID());
        initialValues.put(KEY_SONG_TITLE, favoriteSong.getTitle());
        initialValues.put(KEY_SONG_ARTIST, favoriteSong.getArtist());
        initialValues.put(KEY_SONG_ALBUM, favoriteSong.getAlbum());
        initialValues.put(KEY_SONG_PATH, favoriteSong.getPath());

        mDb.insert(DATABASE_TABLE, null, initialValues);
        return true;
    }

    public boolean deleteSong(String songPath) {

        return mDb.delete(DATABASE_TABLE, KEY_SONG_PATH+ "=" + songPath, null) > 0;
    }

    ArrayList<Song> fetchAllSongs() {
        ArrayList<Song> songList = new ArrayList<>();

        Cursor musicCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_SONG_ID, KEY_SONG_TITLE, KEY_SONG_ARTIST, KEY_SONG_ALBUM, KEY_SONG_PATH}, null, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                long thisId = musicCursor.getLong(1);
                String thisTitle = musicCursor.getString(2);
                String thisArtist = musicCursor.getString(3);
                String thisAlbum = musicCursor.getString(4);
                String thisPath = musicCursor.getString(5);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisPath));
            }
            while (musicCursor.moveToNext());

            musicCursor.close();
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        return songList;
    }
}

