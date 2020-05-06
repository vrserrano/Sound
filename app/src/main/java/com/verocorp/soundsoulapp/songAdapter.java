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
        /* TODO Auto-generated method stub */
        return songs.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            /* TODO Auto-generated method stub */
            return 0;
        }

        @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //map to song layout
                @SuppressLint("ViewHolder") LinearLayout songLay = (LinearLayout)songInf.inflate
                        (R.layout.song, parent, false);
                //get title and artist views
                TextView songView = songLay.findViewById(R.id.song_title);
                TextView artistView = songLay.findViewById(R.id.song_artist);
                //get song using position
                Song currSong = songs.get(position);
                //get title and artist strings
                songView.setText(currSong.getTitle());
                artistView.setText(currSong.getArtist());
                //set position as tag
                songLay.setTag(position);
                return songLay;
            }

        }



