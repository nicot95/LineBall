package mygames.lineball.Music;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

import mygames.lineball.R;

public class MusicHandler {


    private static MediaPlayer backgroundSong;

    private MediaPlayer shape1;
    private MediaPlayer shape2;
    private MediaPlayer shape3;
    private MediaPlayer shape4;
    private MediaPlayer shape5;

    private Random gen;

    public MusicHandler (Context context) {
        this.backgroundSong = MediaPlayer.create(context, R.raw.pamgea);
        backgroundSong.setLooping(true);

        this.shape1         = MediaPlayer.create(context, R.raw.shape1);
        this.shape2         = MediaPlayer.create(context, R.raw.shape2);
        this.shape3         = MediaPlayer.create(context, R.raw.shape3);
        this.shape4         = MediaPlayer.create(context, R.raw.shape4);
        this.shape5         = MediaPlayer.create(context, R.raw.shape5);

        this.gen            = new Random();
    }

    public void playBackgroundMusic() {
        backgroundSong.start();
    }

    public void playShapeCompleted() {
        int random = gen.nextInt(2);
        if (random == 0) {
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
