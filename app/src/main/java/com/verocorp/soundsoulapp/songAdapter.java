package com.verocorp.soundsoulapp;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class songAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    songAdapter(Context c, ArrayList<Song> theSongs) {
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
        public int getCount() {
        return songs.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //mapa donde estan las caciones
                @SuppressLint("ViewHolder") LinearLayout songLay = (LinearLayout)songInf.inflate
                        (R.layout.song, parent, false);
                //vista del titulo y artista de las canciones
                TextView songView = songLay.findViewById(R.id.song_title);
                TextView artistView = songLay.findViewById(R.id.song_artist);
                //conseguimos las canciones por su posición
                Song currSong = songs.get(position);

                songView.setText(currSong.getTitle());
                artistView.setText(currSong.getArtist());
                //se estable la posición como etiqueta
                songLay.setTag(position);
                return songLay;
            }

        }



