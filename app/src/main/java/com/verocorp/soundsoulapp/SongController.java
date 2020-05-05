package com.verocorp.soundsoulapp;

import android.content.Context;
import android.widget.MediaController;

public class SongController extends MediaController {
    public SongController(Context c){
        super(c);
    }

    public SongController(MainActivity mainActivity) {
        super(mainActivity);
    }

    public void hide(){}
}
