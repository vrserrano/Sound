package com.verocorp.soundsoulapp;

class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private String path;

    Song(long songID, String songTitle, String songArtist, String songAlbum, String songPath) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        album = songAlbum;
        path = songPath;
    }

    long getID() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getArtist() {
        return artist;
    }

    String getAlbum() { return album; }

    String getPath() {
        return path;
    }
}
