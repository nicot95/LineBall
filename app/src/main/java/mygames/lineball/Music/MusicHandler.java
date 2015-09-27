package mygames.lineball.Music;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

import mygames.lineball.R;

public class MusicHandler {


    private MediaPlayer gameBackgroundSong;
    private MediaPlayer menuBackgroundSong;
    private MediaPlayer gameOver;
    private MediaPlayer fiveSecsLeft;

    private MediaPlayer shape1;
    private MediaPlayer shape2;

    private Random gen;

    public MusicHandler (Context context) {
        this.gameBackgroundSong = MediaPlayer.create(context, R.raw.pamgea);
        this.menuBackgroundSong = MediaPlayer.create(context, R.raw.builder);
        this.gameOver = MediaPlayer.create(context, R.raw.game_over);
        this.fiveSecsLeft = MediaPlayer.create(context, R.raw.timer);
        menuBackgroundSong.setVolume(0.1f,0.1f);
        gameBackgroundSong.setVolume(0.5f, 0.5f);
        fiveSecsLeft.setVolume(0.7f, 0.7f);
        menuBackgroundSong.setLooping(true);
        gameBackgroundSong.setLooping(true);


        this.shape1         = MediaPlayer.create(context, R.raw.shape1short);
        this.shape2         = MediaPlayer.create(context, R.raw.shape2short);
        this.shape1.setVolume(0.5f, 0.5f);
        this.shape2.setVolume(0.5f, 0.5f);


        this.gen            = new Random();
    }

    public void playGameBackgroundMusic() {
        gameBackgroundSong.start();
    }

    public void playMenuBackgroundMusic() {menuBackgroundSong.start();}

    public void playGameOverMusic() {gameOver.start();}

    public void playFiveSecsLeftMusic() {
        fiveSecsLeft.start();}



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
        if (gameBackgroundSong.isPlaying()) {
            gameBackgroundSong.stop();
        }
    }

    public void stopMenuMusic() {
        if (menuBackgroundSong.isPlaying()) {
            menuBackgroundSong.stop();
        }
    }

    public void stopTimer() {
        if(fiveSecsLeft.isPlaying()) {
            fiveSecsLeft.stop();
        }
    }

}
