package mygames.lineball.Music;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

import mygames.lineball.R;

public class MusicHandler {


    private MediaPlayer backgroundSong;

    private MediaPlayer shape1;
    private MediaPlayer shape2;

    private Random gen;

    public MusicHandler (Context context) {
        this.backgroundSong = MediaPlayer.create(context, R.raw.pamgea);
        backgroundSong.setLooping(true);

        this.shape1         = MediaPlayer.create(context, R.raw.shape1short);
        this.shape2         = MediaPlayer.create(context, R.raw.shape2short);


        this.gen            = new Random();
    }

    public void playBackgroundMusic() {
        backgroundSong.start();
    }

    public void playShapeCompleted() {
        //Stops mediaplayers so no overlap occurs.
        if (shape1.isPlaying()) {
            shape2.start();
            return;
        }
        if (shape2.isPlaying()){
            shape1.start();
            return;
        }

        boolean random = gen.nextBoolean();
        if (random) {
            shape1.start();
        } else {
            shape2.start();
        }
    }

    public void stopMusic() {
        if (backgroundSong.isPlaying()) {
            backgroundSong.stop();
        }
    }

}
