package com.verocorp.soundsoulapp;

class Song {

    private long id;
    private String title;
    private String artist;

    Song(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }

    long getID(){return id;}
    String getTitle(){return title;}
    String getArtist(){return artist;}

}
